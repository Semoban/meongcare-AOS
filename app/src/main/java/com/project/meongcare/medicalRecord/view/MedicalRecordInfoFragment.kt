package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordInfoBinding
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToSimpleDate
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToSimpleTime
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // Todo: 전달받은 진료기록 아이디 연결, 액세스 토큰 userViewModel로 연결
        medicalRecordId = 1
        setMedicalRecord()
        getMedicalRecord()
        initBackBtn()
        initDeleteBtn()
    }

    private fun initDeleteBtn() {
        binding.imagebuttonMedicalrecordinfoDelete.setOnClickListener {
            binding.includeMedicalrecordinfoDeleteDialog.root.visibility = View.VISIBLE
            initDeleteDialog()
        }
    }

    private fun initDeleteDialog() {
        binding.includeMedicalrecordinfoDeleteDialog.run {
            buttonDeleteDialogCancel.setOnClickListener {
                binding.includeMedicalrecordinfoDeleteDialog.root.visibility = View.GONE
            }
            buttonDeleteDialogDelete.setOnClickListener {
                medicalRecordViewModel.deleteMedicalRecordList(
                    intArrayOf(medicalRecordId.toInt()),
                    accessToken,
                )
                isDeleteSuccess()
            }
        }
    }

    private fun isDeleteSuccess() {
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

    private fun initBackBtn() {
        binding.imagebuttonMedicalrecordinfoBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setMedicalRecord() {
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

    private fun getMedicalRecord() {
        medicalRecordViewModel.getMedicalRecord(medicalRecordId)
    }

    private fun testGetRecord(it: Response<MedicalRecordGet>) {
        Log.d("진료기록 테스트", it.toString())
        Log.d("진료기록 테스트2", it.body().toString())
    }
}
