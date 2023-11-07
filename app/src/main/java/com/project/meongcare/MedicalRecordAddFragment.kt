
package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentMedicalRecordAddBinding

class MedicalRecordAddFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordAddBinding.inflate(inflater)
        return binding.root
    }
}