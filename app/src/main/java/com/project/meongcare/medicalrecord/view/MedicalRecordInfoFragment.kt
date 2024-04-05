package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordInfoBinding
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToSimpleDate
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToSimpleTime
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
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
        accessToken =  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTgsImV4cCI6MTcxMjMzMjE4Mn0.EVtsKue6RNJ9B_5imPJRHwg1VzhyCTfT0b5B7LObDKA"

        medicalRecordViewModel.medicalRecord.observe(viewLifecycleOwner) {
            if (it.code() == 200) {
                val record = it.body() as MedicalRecordGet
                setImg(record)
                setDate(record)
                setTime(record)
                setHospital(record)
                setVeterinarian(record)
                setNote(record)
            }
        }

        initMedicalRecord()

    }

    private fun setImg(record: MedicalRecordGet) {
        if (!record.imageUrl.isNullOrBlank()) {
            binding.imageviewMedicalrecordinfoCarrier.visibility = View.GONE
            Glide.with(this@MedicalRecordInfoFragment)
                .load(record.imageUrl)
                .into(binding.imageviewMedicalrecordinfoImage)
        }
    }

    private fun setNote(record: MedicalRecordGet) {
        binding.textViewMedicalrecordinfoNoteDetail.text = record.note
        binding.textviewMedicalrecordinfoNoteCount.text =
            getString(R.string.medicalrecord_note_length, record.note.length)
    }

    private fun initMedicalRecord() {
        medicalRecordViewModel.getMedicalRecord(medicalRecordId, accessToken)
    }

    private fun setVeterinarian(record: MedicalRecordGet) {
        binding.textViewMedicalrecordinfoVeterinarianName.text = record.doctorName
        binding.textviewMedicalrecordinfoVeterinarianNameCount.text =
            getString(R.string.medicalrecord_veterinarian_name_length, record.doctorName.length)
    }

    private fun setHospital(record: MedicalRecordGet) {
        binding.textviewMedicalrecordinfoHospitalName.text = record.hospitalName
        binding.textviewMedicalrecordinfoHospitalNameCount.text =
            getString(R.string.medicalrecord_hospital_name_length, record.hospitalName.length)
    }

    private fun setTime(record: MedicalRecordGet) {
        binding.textvuewMedicalrecordinfoTime.text = convertMDateToSimpleTime(record.dateTime)
    }

    private fun setDate(record: MedicalRecordGet) {
        binding.textvuewMedicalrecordinfoSelectDate.text = convertMDateToSimpleDate(record.dateTime)
    }

    private fun testGetRecord(it: Response<MedicalRecordGet>) {
        Log.d("진료기록 테스트", it.toString())
        Log.d("진료기록 테스트2", it.body().toString())
    }
}
