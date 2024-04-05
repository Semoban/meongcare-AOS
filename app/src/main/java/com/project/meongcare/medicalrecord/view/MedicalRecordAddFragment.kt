package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordAddBinding
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordDateBottomSheetDialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicalRecordAddFragment : Fragment(),
    MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener {
    private lateinit var binding: FragmentMedicalRecordAddBinding
    private var addSelectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordAddBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDateBtn()
        initHospitalName()
        initVeterinarianName()
        initNote()
    }

    private fun initNote() {
        binding.edittextMedicalrecordaddNoteDetail.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                binding.textviewMedicalrecordaddNoteCount.text =
                    getString(R.string.medicalrecord_note_length, textLength)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun initVeterinarianName() {
        binding.edittextMedicalrecordaddVeterinarianName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                binding.textviewMedicalrecordaddVeterinarianNameCount.text =
                    getString(R.string.medicalrecord_veterinarian_name_length, textLength)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun initHospitalName() {
        binding.edittextMedicalrecordaddHospitalName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                binding.textviewMedicalrecordaddHospitalNameCount.text =
                    getString(R.string.medicalrecord_hospital_name_length, textLength)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun initDateBtn() {
        binding.textviewMedicalrecordaddSelectDate.setOnClickListener {
            showCalendarBottomSheet(parentFragmentManager, this@MedicalRecordAddFragment)
        }
    }

    fun showCalendarBottomSheet(
        parentFragmentManager: FragmentManager,
        onDateSelectedListener: MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener,
    ) {
        val bottomSheetDialogFragment = MedicalRecordDateBottomSheetDialogFragment()
        bottomSheetDialogFragment.setOnDateSelecetedListener(onDateSelectedListener)
        bottomSheetDialogFragment.show(
            parentFragmentManager,
            "MedicalRecordDateBottomSheetDialogFragment"
        )
    }

    override fun onDateSelected(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
        val formatterToAdd = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
        addSelectedDate = date.format(formatterToAdd)
        binding.textviewMedicalrecordaddSelectDate.text = date.format(formatter)
        Log.d("MedicalRecordAddFragment", "Selected date: $addSelectedDate")
    }
}
