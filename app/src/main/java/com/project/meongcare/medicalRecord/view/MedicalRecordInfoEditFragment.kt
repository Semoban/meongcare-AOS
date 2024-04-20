package com.project.meongcare.medicalRecord.view

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordInfoEditBinding
import com.project.meongcare.medicalRecord.model.data.local.OnPictureChangedListener
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordDateBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordPictureBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MedicalRecordInfoEditFragment :
    Fragment(),
    MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener,
    OnPictureChangedListener {
    lateinit var binding: FragmentMedicalRecordInfoEditBinding
    private lateinit var mainActivity: MainActivity
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    lateinit var record: MedicalRecordGet
    private var addSelectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getMedicalRecordFromInfo()
        binding = FragmentMedicalRecordInfoEditBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMedicalRecord()
        initCancelBtn()
    }

    private fun initCancelBtn() {
        binding.layoutMedicalrecordinfoeditNoteRecord.buttonFootertwoFirst.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initMedicalRecord() {
        initImg()
        initDate()
        initTime()
        initHospital()
        initVeterinarian()
        initNote()
    }

    private fun initImg() {
        if (!record.imageUrl.isNullOrBlank()) {
            binding.layoutMedicalrecordinfoeditPictureSample.visibility = View.GONE
            Glide.with(this@MedicalRecordInfoEditFragment)
                .load(record.imageUrl)
                .into(binding.imageviewMedicalrecordinfoeditImage)
        }
        binding.cardviewMedicalrecordinfoeditImage.setOnClickListener {
            showPictureBottomSheet()
        }
    }

    private fun initNote() {
        val editText = binding.edittextMedicalrecordinfoeditNoteDetail
        val count = binding.textviewMedicalrecordinfoeditNoteCount
        editText.setText(record.note)
        count.text =
            getString(R.string.medicalrecord_note_length, record.note.length)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun initVeterinarian() {
        val editText = binding.edittextMedicalrecordinfoeditVeterinarianName
        val count = binding.textviewMedicalrecordinfoeditVeterinarianNameCount
        editText.setText(record.doctorName)
        count.text =
            getString(R.string.medicalrecord_veterinarian_name_length, record.doctorName.length)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun initHospital() {
        val editText = binding.edittextMedicalrecordinfoeditHospitalName
        val count = binding.textviewMedicalrecordinfoeditHospitalNameCount
        editText.setText(record.hospitalName)
        count.text =
            getString(R.string.medicalrecord_hospital_name_length, record.hospitalName.length)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun initTime() {
        binding.textviewMedicalrecordinfoeditTreatmentTimeValue.text =
            MedicalRecordUtils.convertMDateToSimpleTime(record.dateTime)
        binding.textviewMedicalrecordinfoeditTreatmentTimeValue.setOnClickListener {
            it.visibility = View.GONE
            binding.timepickerMedicalrecordinfoeditTreatmentTime.visibility = View.VISIBLE
        }
    }

    private fun initDate() {
        binding.textviewMedicalrecordinfoeditSelectDate.text =
            MedicalRecordUtils.convertMDateToSimpleDate(record.dateTime)
        binding.textviewMedicalrecordinfoeditSelectDate.setOnClickListener {
            showCalendarBottomSheet(parentFragmentManager, this@MedicalRecordInfoEditFragment)
        }
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

    private fun showPictureBottomSheet() {
        val bottomSheetFragment = MedicalRecordPictureBottomSheetDialogFragment()

        bottomSheetFragment.setOnPictureChangedListener(this)

        bottomSheetFragment.show(
            parentFragmentManager,
            "MedicalRecordPictureBottomSheetDialogFragment",
        )
    }

    private fun showCalendarBottomSheet(
        parentFragmentManager: FragmentManager,
        onDateSelectedListener: MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener,
    ) {
        val bottomSheetDialogFragment = MedicalRecordDateBottomSheetDialogFragment()
        bottomSheetDialogFragment.setOnDateSelecetedListener(onDateSelectedListener)
        bottomSheetDialogFragment.show(
            parentFragmentManager,
            "MedicalRecordDateBottomSheetDialogFragment",
        )
    }

    private fun setDateAddMode(
        date: LocalDate,
        formatter: DateTimeFormatter?,
    ) {
        binding.textviewMedicalrecordinfoeditSelectDate.run {
            setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
            setTextAppearance(R.style.Typography_Body1_Medium)
            setBackgroundResource(R.drawable.all_rect_r5)
            text = date.format(formatter)
        }
    }

    override fun onPictureChanged(uri: Uri) {
        medicalRecordViewModel.getMedicalRecordImgUri(uri)
        Glide.with(this@MedicalRecordInfoEditFragment)
            .load(uri)
            .into(binding.imageviewMedicalrecordinfoeditImage)
        binding.imageviewMedicalrecordinfoeditImage.visibility = View.VISIBLE
        binding.layoutMedicalrecordinfoeditPictureSample.visibility = View.GONE
    }

    override fun onDateSelected(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
        val formatterToAdd = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
        addSelectedDate = date.format(formatterToAdd)
        setDateAddMode(date, formatter)

        Log.d("MedicalRecordAddFragment", "Selected date: $addSelectedDate")
    }

}