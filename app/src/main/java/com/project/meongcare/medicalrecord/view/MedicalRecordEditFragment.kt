package com.project.meongcare.medicalrecord.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordEditBinding
import com.project.meongcare.medicalrecord.model.data.local.MedicalRecordItemCheckListener
import com.project.meongcare.medicalrecord.viewmodel.DogViewModel
import com.project.meongcare.medicalrecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalrecord.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalRecordEditFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordEditBinding

    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    private lateinit var medicalRecordEditListAdapter: MedicalRecordEditListAdapter

    private var selectedDate = ""
    private var accessToken = ""
    private var dogId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedDate = arguments?.getString("selectedDate")!!
        Log.d("selectedDate", selectedDate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        medicalRecordEditListAdapter =
            MedicalRecordEditListAdapter(
                object : MedicalRecordItemCheckListener {
                    override fun onMedicalRecordItemChecked(medicalRecordIds: IntArray) {
                        for (id in medicalRecordIds) {
                            Log.e("id", "$id")
                        }
                    }
                }
            )

        initMedicalRecordHeader()
        initBackButton()
        initCancelButton()
        initMedicalRecordRecyclerView()
        initDogId()
    }

    private fun initMedicalRecordHeader() {
        dogViewModel.dogNamePreferencesLiveData.observe(viewLifecycleOwner) { dogName ->
            if (dogName != null) {
                val str = getString(R.string.medicalrecord_pet)
                binding.textviewMedicalrecordeditHeaderTitle.text = str.format(dogName)
            }
        }
    }

    private fun initBackButton() {
        binding.imagebuttonMedicalrecordeditBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initCancelButton() {
        binding.layoutMedicalrecordeditFooter.buttonFootertwoFirst.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getMedicalRecordList() {
        medicalRecordViewModel.getMedicalRecordList(
            dogId,
            selectedDate + "T00:00:00",
            accessToken,
        )
        medicalRecordViewModel.medicalRecordList.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.code() == 200) {
                    medicalRecordEditListAdapter.submitList(response.body()?.records)
                }
            }
        }
    }

    private fun initMedicalRecordRecyclerView() {
        binding.recyclerviewMedicalrecordedit.run {
            adapter = medicalRecordEditListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initDogId() {
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
                initAccessToken()
            }
        }
    }

    private fun initAccessToken() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                getMedicalRecordList()
            }
        }
    }
}
