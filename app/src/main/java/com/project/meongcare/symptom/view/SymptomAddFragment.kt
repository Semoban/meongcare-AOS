package com.project.meongcare.symptom.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomAddBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.time.LocalDate

class SymptomAddFragment : Fragment() {
    lateinit var fragmentSymptomAddBinding: FragmentSymptomAddBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomAddBinding = FragmentSymptomAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.detachBottomNav()

        val navController = findNavController()

        symptomViewModel = mainActivity.symptomViewModel

        symptomViewModel.run {
            addSymptomItemTitle.observe(viewLifecycleOwner) { title ->
                if(title != null){
                    fragmentSymptomAddBinding.run {
                        includeItemSymptomAdd.run {
                            imageViewItemSymptomAdd.setImageResource(addSymptomItemImgId.value!!)
                            textViewItemSymptomAdd.text = title.trim()
                        }
                    }
                    symptomViewModel.addSymptomItemVisibility.value = View.VISIBLE
                }
            }

            addSymptomDateText.observe(viewLifecycleOwner) {
                if(it != null){
                    fragmentSymptomAddBinding.run {
                        textViewSymptomAddDate.run {
                            text = symptomViewModel.addSymptomDateText.value
                            setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                            setTextAppearance(R.style.Typography_Body1_Medium)
                        }
                        buttonSymptomAddDate.setBackgroundResource(R.drawable.all_rect_gray1_r5)
                    }
                }
            }

            addSymptomItemVisibility.observe(viewLifecycleOwner) {
                if (it == View.VISIBLE) {
                    fragmentSymptomAddBinding.run {
                        textViewSymptomAddSelectSymptom.run {
                            text = "증상을 선택해주세요"
                            setTextColor(ContextCompat.getColor(mainActivity, R.color.gray4))
                        }

                        buttonSymptomAddSelectSymptom.setBackgroundResource(R.drawable.all_rect_gray1_r5)
                    }
                }
            }
        }

        fragmentSymptomAddBinding.run {
            toolbarSymptomAdd.run {
                setNavigationOnClickListener {
                    Log.d("클릭","네비게이션 버튼 클릭함")
                    navController.navigate(R.id.action_symptomAdd_to_symptom)
                }
            }
            includeBottomsheetSymptomAddDate.run {
                initializeBottomSheet(layoutBottomsheetSymptomAddDate)
                bottomSheetEvent(buttonBottomsheetSymptomAddDateComplete)
                val datePickerHeaderId =
                    datepickerBottomsheetSymptomAddDate.getChildAt(0)
                        .resources.getIdentifier("date_picker_header", "id", "android")
                datepickerBottomsheetSymptomAddDate.findViewById<View>(datePickerHeaderId).visibility =
                    View.GONE
            }

            buttonSymptomAddDate.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            isNullDate()

            timepickerSymptomAdd.run {
                setOnTimeChangedListener { timePicker, hour, minute ->
                    symptomViewModel.addSymptomTimeHour = hour
                    symptomViewModel.addSymptomTimeMinute = minute
                }
            }

            isNullTimePickerValue()

            buttonSymptomAddSelectSymptom.setOnClickListener {
                navController.navigate(R.id.action_symptomAdd_to_symptomSelect)
            }

            isNullAddItem()

            editTextSymptomAddCustom.setOnEditorActionListener { _, actionId, keyEvent ->
                if (
                    (
                        actionId == EditorInfo.IME_ACTION_DONE ||
                            (
                                keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN &&
                                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                            )
                    ) && editTextSymptomAddCustom.text.trim().isNotEmpty()
                ) {
                    layoutSymptomAddList.visibility = View.VISIBLE
                    includeItemSymptomAdd.run {
                        symptomViewModel.run {
                            addSymptomItemImgId.value = R.drawable.symptom_stethoscope
                            addSymptomItemTitle.value = editTextSymptomAddCustom.text.toString().trim()
                        }
                    }
                    editTextSymptomAddCustom.text.clear()
                    editTextSymptomAddCustom.clearFocus()
                    hideKeyboard(editTextSymptomAddCustom)
                    return@setOnEditorActionListener true
                }
                false
            }

            buttonSymptomAddToSymptom.setOnClickListener {
                val dateTimeString =
                    if (!symptomViewModel.addSymptomDateText.value.isNullOrEmpty()) {
                        "${symptomViewModel.addSymptomDateText.value}T${
                            String.format(
                                "%02d:%02d",
                                timepickerSymptomAdd.hour,
                                timepickerSymptomAdd.minute,
                            )
                        }:00"
                    } else {
                        isNullInput(textViewSymptomAddDate, buttonSymptomAddDate)
                        null
                    }

                val addItemName =
                    if (layoutSymptomAddList.visibility == View.VISIBLE) {
                        getSymptomName(symptomViewModel.addSymptomItemImgId.value!!)
                    } else {
                        isNullInput(textViewSymptomAddSelectSymptom, buttonSymptomAddSelectSymptom)
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
        return fragmentSymptomAddBinding.root
    }

    private fun FragmentSymptomAddBinding.isNullAddItem() {
        if (!symptomViewModel.addSymptomItemTitle.value.isNullOrEmpty()) {
            layoutSymptomAddList.visibility = View.VISIBLE
            includeItemSymptomAdd.run {
                imageViewItemSymptomAdd.setImageResource(symptomViewModel.addSymptomItemImgId.value!!)
                textViewItemSymptomAdd.text = symptomViewModel.addSymptomItemTitle.value!!.trim()
            }
        } else {
            layoutSymptomAddList.visibility = View.GONE
        }
    }

    private fun FragmentSymptomAddBinding.isNullTimePickerValue() {
        if (symptomViewModel.addSymptomTimeHour != null && symptomViewModel.addSymptomTimeMinute != null) {
            timepickerSymptomAdd.run {
                hour = symptomViewModel.addSymptomTimeHour!!
                minute = symptomViewModel.addSymptomTimeMinute!!
            }
        }
    }

    private fun FragmentSymptomAddBinding.isNullDate() {
        if (!symptomViewModel.addSymptomDateText.value.isNullOrEmpty()) {
            textViewSymptomAddDate.run {
                text = symptomViewModel.addSymptomDateText.value
                setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                setTextAppearance(R.style.Typography_Body1_Medium)
            }
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

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getSymptomName(symptomImg: Int): String {
        return when (symptomImg) {
            R.drawable.all_weighing_machine -> SymptomType.WEIGHT_LOSS.symptomName
            R.drawable.all_temperature_measurement -> SymptomType.HIGH_FEVER.symptomName
            R.drawable.symptom_cough -> SymptomType.COUGH.symptomName
            R.drawable.symptom_diarrhea -> SymptomType.DIARRHEA.symptomName
            R.drawable.symptom_loss_appetite -> SymptomType.LOSS_OF_APPETITE.symptomName
            R.drawable.symptom_amount_activity -> SymptomType.ACTIVITY_DECREASE.symptomName
            else -> SymptomType.ETC.symptomName
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
}

