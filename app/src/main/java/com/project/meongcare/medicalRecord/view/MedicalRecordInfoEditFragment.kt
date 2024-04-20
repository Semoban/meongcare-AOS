package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordInfoEditBinding
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalRecordInfoEditFragment : Fragment() {
    lateinit var binding: FragmentMedicalRecordInfoEditBinding
    lateinit var record: MedicalRecordGet
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getMedicalRecordFromInfo()
        binding = FragmentMedicalRecordInfoEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMedicalRecord()
    }

    private fun setMedicalRecord() {
        setImg()
        setDate()
        setTime()
        setHospital()
        setVeterinarian()
        setNote()
    }

    private fun setImg() {
        if (!record.imageUrl.isNullOrBlank()) {
            binding.imageviewMedicalrecordinfoeditCarrier.visibility = View.GONE
            Glide.with(this@MedicalRecordInfoEditFragment)
                .load(record.imageUrl)
                .into(binding.imageviewMedicalrecordinfoeditImage)
        }
    }

    private fun setNote() {
        val editText = binding.edittextMedicalrecordinfoeditNoteDetail
        val count = binding.textviewMedicalrecordinfoeditNoteCount
        editText.setText(record.note)
        count.text =
            getString(R.string.medicalrecord_note_length, record.note.length)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun setVeterinarian() {
        val editText = binding.edittextMedicalrecordinfoeditVeterinarianName
        val count = binding.textviewMedicalrecordinfoeditVeterinarianNameCount
        editText.setText(record.doctorName)
        count.text =
            getString(R.string.medicalrecord_veterinarian_name_length, record.doctorName.length)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun setHospital() {
        val editText = binding.edittextMedicalrecordinfoeditHospitalName
        val count = binding.textviewMedicalrecordinfoeditHospitalNameCount
        editText.setText(record.hospitalName)
        count.text =
            getString(R.string.medicalrecord_hospital_name_length, record.hospitalName.length)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun setTime() {
        binding.textviewMedicalrecordinfoeditTreatmentTimeValue.text =
            MedicalRecordUtils.convertMDateToSimpleTime(record.dateTime)
    }

    private fun setDate() {
        binding.textviewMedicalrecordinfoeditSelectDate.text =
            MedicalRecordUtils.convertMDateToSimpleDate(record.dateTime)
    }

    private fun getMedicalRecordFromInfo() {
        if (arguments != null) {
            val medicalRecordGet = arguments?.getParcelable<MedicalRecordGet>("medicalRecord")
            if (medicalRecordGet != null) {
                record = medicalRecordGet
                Log.d("진료기록 수정 화면", record.toString())
            }
        }
    }

    private fun setEditTextWatcher(
        editText: EditText,
        count: TextView,
        stringId: Int,
    ) {
        editText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                    count.text =
                        getString(stringId, p0?.length ?: 0)
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            },
        )
    }

}