package com.project.meongcare.medicalrecord.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentMedicalRecordEditBinding

class MedicalRecordEditFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordEditBinding
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
}
