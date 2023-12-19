package com.project.meongcare.symptom.view

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomEditBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToMonthDate
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToSimpleTime
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertSimpleDateToMonthDate
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomEditFragment : Fragment() {
    lateinit var fragmentSymptomEditBinding: FragmentSymptomEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomEditBinding = FragmentSymptomEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        symptomViewModel = mainActivity.symptomViewModel
        navController = findNavController()

        symptomViewModel.run {
            symptomDateText.observe(viewLifecycleOwner) {
                if (it != null) {
                    fragmentSymptomEditBinding.textViewSymptomEditDate.text =
                        convertDateToMonthDate(it)
                }
            }
            symptomItemTitle.observe(viewLifecycleOwner) {
                fragmentSymptomEditBinding.includeItemSymptomEdit.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = it
                }
            }

            selectCheckedImg.observe(viewLifecycleOwner) {
                fragmentSymptomEditBinding.includeItemSymptomEdit.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = symptomViewModel.symptomItemTitle.value
                }
            }
        }

        fragmentSymptomEditBinding.run {
            val symptomData = symptomViewModel.infoSymptomData.value!!
            includeBottomsheetSymptomEdit.run {
                initializeBottomSheet(layoutBottomsheetSymptomAddDate)
                bottomSheetEvent(buttonBottomsheetSymptomAddDateComplete)
                removeDatePickerHeader()
            }

            textViewSymptomEditDate.run {
                text = convertDateToMonthDate(symptomViewModel.symptomDateText.value!!)
                setOnClickListener {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            textViewSymptomEditTime.run {
                text = convertDateToTime(symptomData.dateTime)
            }
            buttonSymptomEditTime.setOnClickListener {
                it.visibility = View.GONE
                timepickerSymptomEdit.visibility = View.VISIBLE
            }

            timepickerSymptomEdit.run {
                setOnTimeChangedListener { timePicker, hour, minute ->
                    symptomViewModel.symptomTimeHour = hour
                    symptomViewModel.symptomTimeMinute = minute
                }
            }

            buttonSymptomEditSelectSymptom.setOnClickListener {
                symptomViewModel.isEditSymptom = true
                navController.navigate(R.id.action_symptomEdit_to_symptomSelect)
            }

            editTextSymptomEditCustom.setOnEditorActionListener { _, actionId, keyEvent ->
                hideCompleteBtn()
                if (
                    (
                        actionId == EditorInfo.IME_ACTION_DONE ||
                            (
                                keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN &&
                                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                            )
                    ) && editTextSymptomEditCustom.text.trim().isNotEmpty()
                ) {
                    layoutItemSymptomEdit.visibility = View.VISIBLE
                    includeItemSymptomEdit.run {
                        symptomViewModel.run {
                            symptomItemImgId.value = R.drawable.symptom_stethoscope
                            symptomItemTitle.value =
                                editTextSymptomEditCustom.text.toString().trim()
                        }
                    }
                    editTextSymptomEditCustom.text.clear()
                    editTextSymptomEditCustom.clearFocus()
                    showCompleteBtn()
                    SymptomUtils.hideKeyboard(editTextSymptomEditCustom)
                    return@setOnEditorActionListener true
                }
                false
            }

            includeItemSymptomEdit.run {
                imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                textViewItemSymptomAdd.text = symptomViewModel.symptomItemTitle.value
            }

            buttonSymptomEditCancel.setOnClickListener {
                navController.navigate(R.id.action_symptomEdit_to_symptomInfo)
            }

            buttonSymptomEditComplete.setOnClickListener {
                val dateTimeString =
                    if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
                        val date =
                            convertSimpleDateToMonthDate(symptomViewModel.symptomDateText.value!!)
                        if (timepickerSymptomEdit.visibility == View.VISIBLE) {
                            "${date}T${
                                String.format(
                                    "%02d:%02d",
                                    timepickerSymptomEdit.hour,
                                    timepickerSymptomEdit.minute,
                                )
                            }:00"
                        } else {
                            "${date}T${convertDateToSimpleTime(symptomData.dateTime)}"
                        }
                    } else {
                        isNullInput(textViewSymptomEditDate, buttonSymptomEditDate)
                        null
                    }

                val symptomItemName =
                    if (layoutItemSymptomEdit.visibility == View.VISIBLE) {
                        SymptomUtils.getSymptomName(symptomViewModel.symptomItemImgId.value!!)
                    } else {
                        isNullInput(
                            textViewSymptomEditSelectSymptom,
                            buttonSymptomEditSelectSymptom,
                        )
                        null
                    }

                val symptomItemTitle = symptomViewModel.symptomItemTitle.value

                if (dateTimeString != null && symptomItemName != null && symptomItemTitle != null) {
                    Log.d("Symptom문제", dateTimeString)
                    val toEditSymptom =
                        ToEditSymptom(
                            symptomData.symptomId,
                            dateTimeString,
                            symptomItemName,
                            symptomItemTitle,
                        )
                    Log.d("Symptom문제", toEditSymptom.toString())
                    SymptomRepository.editSymptom(toEditSymptom)
                    symptomViewModel.clearLiveData()
                    navController.navigate(R.id.action_symptomEdit_to_symptom)
                }
            }
        }
        return fragmentSymptomEditBinding.root
    }

    fun removeDatePickerHeader() {
        fragmentSymptomEditBinding.includeBottomsheetSymptomEdit.run {
            val datePickerHeaderId =
                datepickerBottomsheetSymptomAddDate.getChildAt(0)
                    .resources.getIdentifier("date_picker_header", "id", "android")
            datepickerBottomsheetSymptomAddDate.findViewById<View>(datePickerHeaderId).visibility =
                View.GONE
        }
    }

    private fun isNullInput(
        textView: TextView,
        layout: LinearLayout,
    ) {
        textView.run {
            text = "필수 입력 값입니다."
            setTextAppearance(R.style.Typography_Body1_Regular)
            setTextColor(ContextCompat.getColor(mainActivity, R.color.sub1))
        }
        layout.run {
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        }
    }

    private fun initializeBottomSheet(symptomBottomSheet: LinearLayout) {
        // BottomSheetBehavior에 layout 설정
        bottomSheetBehavior = BottomSheetBehavior.from(symptomBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(
            object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                    // BottomSheetBehavior state에 따른 이벤트
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            Log.d("MainActivity", "state: hidden")
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            Log.d("MainActivity", "state: expanded")
                        }

                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            Log.d("MainActivity", "state: collapsed")
                        }

                        BottomSheetBehavior.STATE_DRAGGING -> {
                            Log.d("MainActivity", "state: dragging")
                        }

                        BottomSheetBehavior.STATE_SETTLING -> {
                            Log.d("MainActivity", "state: settling")
                        }

                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            Log.d("MainActivity", "state: half expanded")
                        }
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                }
            },
        )
    }

    private fun bottomSheetEvent(symptomBottomSheetCloseButton: LinearLayout) {
        symptomBottomSheetCloseButton.setOnClickListener {
            val datePicker =
                mainActivity.findViewById<DatePicker>(R.id.datepicker_bottomsheet_symptom_add_date)

            val year = datePicker.year
            val month = datePicker.month + 1
            val dayOfMonth = datePicker.dayOfMonth

            val customDateTime = String.format("%04d-%02d-%02dT00:00:00", year, month, dayOfMonth)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            symptomViewModel.symptomDateText.value = customDateTime.toString()
            Log.d("뷰모델", symptomViewModel.symptomDateText.value.toString())
        }
    }

    private fun hideCompleteBtn() {
        fragmentSymptomEditBinding.layoutSymptomEditButton.visibility = View.GONE
    }

    private fun showCompleteBtn() {
        fragmentSymptomEditBinding.layoutSymptomEditButton.visibility = View.VISIBLE
    }
}
