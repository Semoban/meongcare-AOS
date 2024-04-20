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
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToDBDate
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToDBTime
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordDateBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordPictureBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
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
    private var addTime: String = ""

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
        initCompleteBtn()
    }

    private fun initCompleteBtn() {
        binding.layoutMedicalrecordinfoeditNoteRecord.buttonFootertwoSecond.setOnClickListener {
            if (checkMedicalRecordDataNull()) {
                putMedicalRecord()
                showResultMessage()
            }
        }
    }
    
    private fun putMedicalRecord() {
        if (checkMedicalRecordDataNull()) {
            val medicalRecordId = record.medicalRecordId
            val uri = medicalRecordViewModel.medicalRecordAddImgUri.value
            val date = addSelectedDate
            val timePicker = binding.timepickerMedicalrecordinfoeditTreatmentTime
            val time = if (timePicker.visibility != View.VISIBLE) {
                addTime
            } else {
                String.format(
                    "%02d:%02d:00",
                    timePicker.hour,
                    timePicker.minute,
                )
            }
            val dateTime = "${date}T$time"
            val hospitalName = binding.edittextMedicalrecordinfoeditHospitalName.text.toString()
            val doctorName = binding.edittextMedicalrecordinfoeditVeterinarianName.text.toString()
            val note = binding.edittextMedicalrecordinfoeditNoteDetail.text.toString()

            Log.d(
                "수정 확인",
                "$date $time $dateTime $hospitalName $doctorName $note ${record.imageUrl} "
            )

            medicalRecordViewModel.putMedicalRecord(
                medicalRecordId,
                dateTime,
                hospitalName,
                doctorName,
                note,
                uri ?: Uri.parse(record.imageUrl),
            )
        }
    }

    private fun showResultMessage() {
        medicalRecordViewModel.medicalRecordResponse.observe(viewLifecycleOwner) {
            if (it == 200) {
                findNavController().popBackStack()
                showSuccessSnackbar()
            } else {
                showFailSnackbar()
            }
        }
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            "수정이 완료되었습니다",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            "수정에 실패하였습니다.\n잠시 후 다시 시도해주세요",
        ).show()
    }

    private fun setEditTextClickLister(
        layout: ConstraintLayout,
        editText: EditText,
        count: TextView,
    ) {
        val clickListener =
            View.OnClickListener { setEditTextAttributes(layout, editText, count) }
        editText.setOnClickListener(clickListener)
        layout.setOnClickListener(clickListener)
    }

    private fun setEditTextAttributes(
        layout: ConstraintLayout,
        editText: EditText,
        count: TextView,
    ) {
        editText.inputType = InputType.TYPE_CLASS_TEXT
        layout.setBackgroundResource(R.drawable.all_rect_r5)
        editText.requestFocus()
        editText.setHintTextColor(ContextCompat.getColor(mainActivity, R.color.black))
        editText.hint = ""
        count.visibility = View.VISIBLE
    }

    private fun isEditTextNullOrEmpty(
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

    private fun checkMedicalRecordDataNull(): Boolean {
        val hospitalName = binding.edittextMedicalrecordinfoeditHospitalName.text
        val doctorName = binding.edittextMedicalrecordinfoeditVeterinarianName.text
        val note = binding.edittextMedicalrecordinfoeditNoteDetail.text

        if (hospitalName.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordinfoeditHospitalName
            val layout = binding.layoutMedicalrecordinfoeditHospitalName
            val count = binding.textviewMedicalrecordinfoeditHospitalNameCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (doctorName.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordinfoeditVeterinarianName
            val layout = binding.layoutMedicalrecordinfoeditVeterinarianName
            val count = binding.textviewMedicalrecordinfoeditVeterinarianNameCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (note.isNullOrBlank()) {
            val editText = binding.edittextMedicalrecordinfoeditNoteDetail
            val layout = binding.layoutMedicalrecordinfoeditNote
            val count = binding.textviewMedicalrecordinfoeditNoteCount
            isEditTextNullOrEmpty(editText, layout, count)
        }

        if (hospitalName.isNotBlank() && doctorName.isNotBlank() && note.isNotBlank()) {
            return true
        }
        return false
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
        val layout = binding.layoutMedicalrecordinfoeditNote
        val count = binding.textviewMedicalrecordinfoeditNoteCount
        editText.setText(record.note)
        count.text =
            getString(R.string.medicalrecord_note_length, record.note.length)
        setEditTextClickLister(layout, editText, count)
        setEditTextWatcher(editText, count, R.string.medicalrecord_note_length)
    }

    private fun initVeterinarian() {
        val editText = binding.edittextMedicalrecordinfoeditVeterinarianName
        val layout = binding.layoutMedicalrecordinfoeditVeterinarianName
        val count = binding.textviewMedicalrecordinfoeditVeterinarianNameCount
        editText.setText(record.doctorName)
        count.text =
            getString(R.string.medicalrecord_veterinarian_name_length, record.doctorName.length)
        setEditTextClickLister(layout, editText, count)
        setEditTextWatcher(editText, count, R.string.medicalrecord_veterinarian_name_length)
    }

    private fun initHospital() {
        val editText = binding.edittextMedicalrecordinfoeditHospitalName
        val layout = binding.layoutMedicalrecordinfoeditHospitalName
        val count = binding.textviewMedicalrecordinfoeditHospitalNameCount
        editText.setText(record.hospitalName)
        count.text =
            getString(R.string.medicalrecord_hospital_name_length, record.hospitalName.length)
        setEditTextClickLister(layout, editText, count)
        setEditTextWatcher(editText, count, R.string.medicalrecord_hospital_name_length)
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
                addSelectedDate = convertMDateToDBDate(record.dateTime)
                addTime = convertMDateToDBTime(record.dateTime)
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
        addSelectedDate = date.format(formatterToAdd)
        setDateAddMode(date, formatter)

        Log.d("MedicalRecordAddFragment", "Selected date: $addSelectedDate")
    }

}