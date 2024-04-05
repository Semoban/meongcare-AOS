package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.project.meongcare.databinding.FragmentMedicalRecordInfoBinding
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response

@AndroidEntryPoint
class MedicalRecordInfoFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordInfoBinding
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    private var accessToken = ""
    private var medicalRecordId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medicalRecordId = 1
        accessToken =  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTgsImV4cCI6MTcxMjMyMjgxNX0.keYXq_bTXsX0o_V2MmnH0X6Ghs9FWUxg46Yb9Ba7tGY"

        medicalRecordViewModel.getMedicalRecord(medicalRecordId,accessToken)

        medicalRecordViewModel.medicalRecord.observe(viewLifecycleOwner) {
            testGetRecord(it)

        }

    }

    private fun testGetRecord(it: Response<MedicalRecordGet>) {
        Log.d("진료기록 테스트", it.toString())
        Log.d("진료기록 테스트2", it.body().toString())
    }
}
