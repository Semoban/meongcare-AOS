package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentMedicalRecordInfoBinding

class MedicalRecordInfoFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordInfoBinding.inflate(inflater)
        return binding.root
    }
}
