
package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentMedicalRecordBinding

class MedicalRecordFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordBinding.inflate(inflater)
        return binding.root
    }
}