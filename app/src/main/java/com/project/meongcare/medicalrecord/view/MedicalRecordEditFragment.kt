package com.project.meongcare.medicalrecord.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordEditBinding
import com.project.meongcare.medicalrecord.viewmodel.DogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalRecordEditFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordEditBinding

    private val dogViewModel: DogViewModel by viewModels()
    private var selectedDate = ""

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

        initMedicalRecordHeader()
    }

    private fun initMedicalRecordHeader() {
        dogViewModel.dogNamePreferencesLiveData.observe(viewLifecycleOwner) { dogName ->
            if (dogName != null) {
                val str = getString(R.string.medicalrecord_pet)
                binding.textviewMedicalrecordeditHeaderTitle.text = str.format(dogName)
            }
        }
    }
}
