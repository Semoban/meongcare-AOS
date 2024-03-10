package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initMedicalRecordListEditButton()
    }

    private fun initMedicalRecordListEditButton() {
        binding.textviewMedicalrecordEdit.setOnClickListener {
            // 선택된 날짜 번들 객체에 전달
            val bundle = Bundle()
            bundle.putString("selectedDate", "")
            findNavController().navigate(R.id.action_medicalRecordFragment_to_medicalRecordEditFragment, bundle)
        }
    }
}
