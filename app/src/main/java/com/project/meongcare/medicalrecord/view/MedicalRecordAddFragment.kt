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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
            checkMedicalRecordDataNull()
        }
    }

    private fun checkMedicalRecordDataNull() {
        if (addSelectedDate.isNullOrEmpty()) {
            isDateNullOrEmpty()
        }

        if (binding.edittextMedicalrecordaddHospitalName.text.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordaddHospitalName
            val layout = binding.layoutMedicalrecordaddHospitalName
            val count = binding.textviewMedicalrecordaddHospitalNameCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (binding.edittextMedicalrecordaddVeterinarianName.text.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordaddVeterinarianName
            val layout = binding.layoutMedicalrecordaddVeterinarianName
            val count = binding.textviewMedicalrecordaddVeterinarianNameCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (binding.edittextMedicalrecordaddNoteDetail.text.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordaddNoteDetail
            val layout = binding.layoutMedicalrecordaddNote
            val count = binding.textviewMedicalrecordaddNoteCount
            isEditTextNullOrEmpty(editText, layout, count)
        }
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
        layout!!.setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        textCount.visibility = View.INVISIBLE
        editText.run {
            setHintTextColor(ContextCompat.getColor(mainActivity, R.color.sub1))
            hint = "필수 입력 값입니다"
        }
        MedicalRecordUtils.hideKeyboard(editText)
    }

    private fun setAddMode(
        view: Any,
        layout: ConstraintLayout?,
        textCount: TextView?,
    ) {
        when (view) {
            is TextView -> {
                view.setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                view.setTextAppearance(R.style.Typography_Body1_Medium)
                view.setBackgroundResource(R.drawable.all_rect_r5)
            }

            is EditText -> {
                layout!!.setBackgroundResource(R.drawable.all_rect_r5)
                view.setHintTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                view.hint = ""
                textCount!!.visibility = View.VISIBLE
            }

            else -> throw IllegalArgumentException("Unsupported view type")
        }
    }

    private fun initImg() {
        binding.cardviewMedicalrecordaddImage.setOnClickListener {
            showPictureBottomSheet()
        }
    }

    private fun initNote() {
        binding.edittextMedicalrecordaddNoteDetail.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    val textLength = s?.length ?: 0
                    binding.textviewMedicalrecordaddNoteCount.text =
                        getString(R.string.medicalrecord_note_length, textLength)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )
    }

    private fun initVeterinarianName() {
        binding.edittextMedicalrecordaddVeterinarianName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    val textLength = s?.length ?: 0
                    binding.textviewMedicalrecordaddVeterinarianNameCount.text =
                        getString(R.string.medicalrecord_veterinarian_name_length, textLength)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )
    }

    private fun initHospitalName() {
        binding.edittextMedicalrecordaddHospitalName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    val textLength = s?.length ?: 0
                    binding.textviewMedicalrecordaddHospitalNameCount.text =
                        getString(R.string.medicalrecord_hospital_name_length, textLength)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )
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
        setAddMode(binding.textviewMedicalrecordaddSelectDate, null, null)
        binding.textviewMedicalrecordaddSelectDate.text = date.format(formatter)

        Log.d("MedicalRecordAddFragment", "Selected date: $addSelectedDate")
    }

    override fun onPictureChanged(uri: Uri) {
        Glide.with(this@MedicalRecordAddFragment)
            .load(uri)
            .into(binding.imageviewMedicalrecordaddImage)
        binding.imageviewMedicalrecordaddImage.visibility = View.VISIBLE
        binding.layoutMedicalrecordaddPictureSample.visibility = View.GONE
    }
}
