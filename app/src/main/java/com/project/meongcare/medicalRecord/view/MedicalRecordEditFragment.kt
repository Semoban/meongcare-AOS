package com.project.meongcare.medicalRecord.view

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
import com.project.meongcare.medicalRecord.model.data.local.MedicalRecordItemCheckListener
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
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
                        setSelectAllCheckBoxState(medicalRecordIds)
                        deleteMedicalRecord(medicalRecordIds)
                    }
                },
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
                    initSelectAllCheckBox()
                    initSelectAllTextView()
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

    private fun initSelectAllCheckBox() {
        binding.run {
            // 전체 선택 체크 버튼 직접 클릭했을 때 제어
            checkboxMedicalrecordeditSelectAll.setOnCheckedChangeListener { _, isChecked ->
                medicalRecordEditListAdapter.changeAllItemsCheckState(isChecked)
            }
        }
    }

    private fun initSelectAllTextView() {
        binding.run {
            textviewMedicalrecordeditDeleteAll.setOnClickListener {
                checkboxMedicalrecordeditSelectAll.isChecked = !checkboxMedicalrecordeditSelectAll.isChecked
            }
        }
    }

    private fun setSelectAllCheckBoxState(medicalRecordIds: IntArray) {
        val checkedIdsCount = medicalRecordIds.size
        val checkBoxCount = medicalRecordViewModel.medicalRecordList.value?.body()?.records?.size ?: 0

        // 목록 체크 상태에 따른 전체 선택 체크 버튼 제어
        binding.checkboxMedicalrecordeditSelectAll.checkedState =
            if (checkBoxCount == checkedIdsCount) {
                MaterialCheckBox.STATE_CHECKED
            } else if (checkedIdsCount == 0) {
                MaterialCheckBox.STATE_UNCHECKED
            } else {
                MaterialCheckBox.STATE_INDETERMINATE
            }
    }

    private fun deleteMedicalRecord(medicalRecordIds: IntArray) {
        binding.run {
            layoutMedicalrecordeditFooter.buttonFootertwoSecond.setOnClickListener {
                medicalRecordViewModel.deleteMedicalRecordList(medicalRecordIds)
                medicalRecordViewModel.deleteMedicalRecordResponse.observe(viewLifecycleOwner) { response ->
                    if (response != null && response == 200) {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_success_16dp,
                            getString(R.string.medicalrecord_delete_success),
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}
