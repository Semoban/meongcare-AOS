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
import com.project.meongcare.databinding.FragmentSymptomAddBinding
import com.project.meongcare.databinding.FragmentSymptomEditBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToMonthDate
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.view.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.time.LocalDate


class SymptomEditFragment : Fragment() {
    lateinit var fragmentSymptomEditBinding: FragmentSymptomEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSymptomEditBinding = FragmentSymptomEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        symptomViewModel = mainActivity.symptomViewModel
        navController = findNavController()

        fragmentSymptomEditBinding.run {
            val symptomData = symptomViewModel.infoSymptomData.value
            textViewSymptomEditDate.text = convertDateToMonthDate(symptomData!!.dateTime)
            textViewSymptomEditTime.text = convertDateToTime(symptomData.dateTime)
            includeItemSymptomEdit.run {
                imageViewItemSymptomAdd.setImageResource(getSymptomImg(symptomData))
                textViewItemSymptomAdd.text = symptomData.note
            }

            includeBottomsheetSymptomEdit.run {
                initializeBottomSheet(layoutBottomsheetSymptomAddDate)
                bottomSheetEvent(buttonBottomsheetSymptomAddDateComplete)
                val datePickerHeaderId =
                    datepickerBottomsheetSymptomAddDate.getChildAt(0)
                        .resources.getIdentifier("date_picker_header", "id", "android")
                datepickerBottomsheetSymptomAddDate.findViewById<View>(datePickerHeaderId).visibility =
                    View.GONE
            }

            textViewSymptomEditDate.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            timepickerSymptomEdit.run {
                setOnTimeChangedListener { timePicker, hour, minute ->
                    symptomViewModel.addSymptomTimeHour = hour
                    symptomViewModel.addSymptomTimeMinute = minute
                }
            }

            buttonSymptomEditSelectSymptom.setOnClickListener {
                navController.navigate(R.id.action_symptomEdit_to_symptomSelect)
            }

            editTextSymptomEditCustom.setOnEditorActionListener { _, actionId, keyEvent ->
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
                            addSymptomItemImgId.value = R.drawable.symptom_stethoscope
                            addSymptomItemTitle.value =
                                editTextSymptomEditCustom.text.toString().trim()
                        }
                    }
                    editTextSymptomEditCustom.text.clear()
                    editTextSymptomEditCustom.clearFocus()
                    SymptomUtils.hideKeyboard(editTextSymptomEditCustom)
                    return@setOnEditorActionListener true
                }
                false
            }

            buttonSymptomEditComplete.setOnClickListener {
                val dateTimeString =
                    if (!symptomViewModel.addSymptomDateText.value.isNullOrEmpty()) {
                        "${symptomViewModel.addSymptomDateText.value}T${
                            String.format(
                                "%02d:%02d",
                                timepickerSymptomEdit.hour,
                                timepickerSymptomEdit.minute,
                            )
                        }:00"
                    } else {
                        isNullInput(textViewSymptomEditDate, buttonSymptomEditDate)
                        null
                    }

                val addItemName =
                    if (layoutItemSymptomEdit.visibility == View.VISIBLE) {
                        SymptomUtils.getSymptomName(symptomViewModel.addSymptomItemImgId.value!!)
                    } else {
                        isNullInput(textViewSymptomEditSelectSymptom, buttonSymptomEditSelectSymptom)
                        null
                    }

                val addItemTitle = symptomViewModel.addSymptomItemTitle.value

                if (dateTimeString != null && addItemName != null && addItemTitle != null) {
                    Log.d("Symptom문제", dateTimeString)
                    val toAddSymptom = ToAddSymptom(1, addItemName, addItemTitle, dateTimeString)
                    Log.d("Symptom문제", toAddSymptom.toString())
                    SymptomRepository.addSymptom(toAddSymptom)
                    symptomViewModel.clearLiveData()
                    navController.navigate(R.id.action_symptomAdd_to_symptom)
                }
            }
        }
        return fragmentSymptomEditBinding.root
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

    private fun bottomSheetEvent(symptomBottomSheetCloseButton: LinearLayout): LocalDate {
        var customDate = LocalDate.now()
        symptomBottomSheetCloseButton.setOnClickListener {
            val datePicker =
                mainActivity.findViewById<DatePicker>(R.id.datepicker_bottomsheet_symptom_add_date)
            customDate = LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            symptomViewModel.addSymptomDateText.value = customDate.toString()
            Log.d("뷰모델", symptomViewModel.addSymptomDateText.value.toString())
        }
        return customDate
    }
}