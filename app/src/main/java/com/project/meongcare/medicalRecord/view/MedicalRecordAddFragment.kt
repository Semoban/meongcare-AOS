package com.project.meongcare.medicalRecord.view

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordAddBinding
import com.project.meongcare.medicalRecord.model.data.local.OnPictureChangedListener
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordDateBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordPictureBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicalRecordAddFragment :
    Fragment(),
    MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener,
    OnPictureChangedListener {
    private lateinit var binding: FragmentMedicalRecordAddBinding
    private lateinit var mainActivity: MainActivity
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()
    private var addSelectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordAddBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initImg()
        initDateBtn()
        initHospitalName()
        initVeterinarianName()
        initNote()
        binding.layoutMedicalrecordaddNoteRecord.buttonFooterone.setOnClickListener {
            if (checkMedicalRecordDataNull()) {
                Log.d("작동널",checkMedicalRecordDataNull().toString())
            }
        }
    }

    private fun checkMedicalRecordDataNull(): Boolean {
        val date = addSelectedDate
        val hospitalName = binding.edittextMedicalrecordaddHospitalName.text
        val doctorName = binding.edittextMedicalrecordaddVeterinarianName.text
        val note = binding.edittextMedicalrecordaddNoteDetail.text

        if (date.isNullOrEmpty()) {
            isDateNullOrEmpty()
        }

        if (hospitalName.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordaddHospitalName
            val layout = binding.layoutMedicalrecordaddHospitalName
            val count = binding.textviewMedicalrecordaddHospitalNameCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (doctorName.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordaddVeterinarianName
            val layout = binding.layoutMedicalrecordaddVeterinarianName
            val count = binding.textviewMedicalrecordaddVeterinarianNameCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (note.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordaddNoteDetail
            val layout = binding.layoutMedicalrecordaddNote
            val count = binding.textviewMedicalrecordaddNoteCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (date.isNotEmpty() && hospitalName.isNotBlank() && doctorName.isNotBlank() && note.isNotBlank()) {
            return true
        }
        return false
    }

    private fun isDateNullOrEmpty() {
        binding.textviewMedicalrecordaddSelectDate.run {
            text = "필수 입력 값입니다."
            binding.imageviewMedicalrecordaddSelectDate.visibility = View.INVISIBLE
            setTextAppearance(R.style.Typography_Body1_Regular)
            setTextColor(ContextCompat.getColor(mainActivity, R.color.sub1))
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        }
    }

    fun isEditTextNullOrEmpty(
        editText: EditText,
        layout: ConstraintLayout?,
        textCount: TextView,
    ) {
        editText.inputType = InputType.TYPE_NULL
        editText.isClickable = true

        layout!!.setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        textCount.visibility = View.INVISIBLE
        editText.run {
            setHintTextColor(ContextCompat.getColor(mainActivity, R.color.sub1))
            hint = "필수 입력 값입니다"
        }
        MedicalRecordUtils.hideKeyboard(editText)
    }

    private fun initImg() {
        binding.cardviewMedicalrecordaddImage.setOnClickListener {
            showPictureBottomSheet()
        }
    }

    private fun initNote() {
        val editText = binding.edittextMedicalrecordaddNoteDetail
        val layout = binding.layoutMedicalrecordaddNote
        val count = binding.textviewMedicalrecordaddNoteCount
        setEditTextClickLister(layout, editText, count)
        setEditTextWatcher(editText, count, R.string.medicalrecord_note_length)
    }

    private fun initVeterinarianName() {
        val editText = binding.edittextMedicalrecordaddVeterinarianName
        val layout = binding.layoutMedicalrecordaddVeterinarianName
        val count = binding.textviewMedicalrecordaddVeterinarianNameCount
        setEditTextClickLister(layout, editText, count)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun initHospitalName() {
        val editText = binding.edittextMedicalrecordaddHospitalName
        val layout = binding.layoutMedicalrecordaddHospitalName
        val count = binding.textviewMedicalrecordaddHospitalNameCount
        setEditTextClickLister(layout, editText, count)
        setEditTextWatcher(editText, count, R.string.medicalrecord_hospital_name_length)
    }

    private fun setEditTextWatcher(editText: EditText, count: TextView, stringId: Int) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                count.text =
                    getString(stringId, p0?.length ?: 0)
            }

        })
    }

    private fun setEditTextClickLister(
        layout: ConstraintLayout,
        editText: EditText,
        count: TextView
    ) {
        val clickListener = View.OnClickListener {
            setEditTextAttributes(layout, editText, count)
        }
        editText.setOnClickListener(clickListener)
        layout.setOnClickListener(clickListener)
    }

    private fun setEditTextAttributes(
        layout: ConstraintLayout,
        editText: EditText,
        count: TextView
    ) {
        editText.inputType = InputType.TYPE_CLASS_TEXT
        layout.setBackgroundResource(R.drawable.all_rect_r5)
        editText.setHintTextColor(ContextCompat.getColor(mainActivity, R.color.black))
        editText.hint = ""
        count.visibility = View.VISIBLE
    }

    private fun initDateBtn() {
        binding.textviewMedicalrecordaddSelectDate.setOnClickListener {
            showCalendarBottomSheet(parentFragmentManager, this@MedicalRecordAddFragment)
        }
    }

    private fun showPictureBottomSheet() {
        val bottomSheetFragment = MedicalRecordPictureBottomSheetDialogFragment()

        bottomSheetFragment.setOnPictureChangedListener(this)

        bottomSheetFragment.show(
            parentFragmentManager,
            "MedicalRecordPictureBottomSheetDialogFragment",
        )
    }

    fun showCalendarBottomSheet(
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

    override fun onDateSelected(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
        val formatterToAdd = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
        addSelectedDate = date.format(formatterToAdd)
        setDateAddMode(date, formatter)

        Log.d("MedicalRecordAddFragment", "Selected date: $addSelectedDate")
    }

    private fun setDateAddMode(date: LocalDate, formatter: DateTimeFormatter?) {
        binding.textviewMedicalrecordaddSelectDate.run {
            setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
            setTextAppearance(R.style.Typography_Body1_Medium)
            setBackgroundResource(R.drawable.all_rect_r5)
            text = date.format(formatter)
        }
    }

    override fun onPictureChanged(uri: Uri) {
        Glide.with(this@MedicalRecordAddFragment)
            .load(uri)
            .into(binding.imageviewMedicalrecordaddImage)
        binding.imageviewMedicalrecordaddImage.visibility = View.VISIBLE
        binding.layoutMedicalrecordaddPictureSample.visibility = View.GONE
    }
}
