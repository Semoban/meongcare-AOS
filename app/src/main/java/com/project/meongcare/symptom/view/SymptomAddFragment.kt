package com.project.meongcare.symptom.view

import android.content.Context
import android.graphics.Paint
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomAddBinding
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
    ): View? {
        fragmentSymptomAddBinding = FragmentSymptomAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.detachBottomNav()

        symptomViewModel = ViewModelProvider(this)[SymptomViewModel::class.java]

        symptomViewModel.run {
            addSymptomItemTitle.observe(viewLifecycleOwner){ title ->
                fragmentSymptomAddBinding.run {
                    includeItemSymptomAdd.run {
                        imageViewItemSymptomAdd.setImageResource(addSymptomItemImgId.value!!)
                        textViewItemSymptomAdd.text = title.trim()
                    }
                }
            }

            addSymptomDateText.observe(viewLifecycleOwner){
                fragmentSymptomAddBinding.run {
                    textViewSymptomAddDate.run {
                        text = symptomViewModel.addSymptomDateText.value
                        setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                        setTextAppearance(R.style.Typography_Body1_Medium)
                    }
                }
            }
        }

        fragmentSymptomAddBinding.run {
            includeBottomsheetSymptomAddDate.run {
                initializeBottomSheet(layoutBottomsheetSymptomAddDate)
                bottomSheetEvent(buttonBottomsheetSymptomAddDateComplete)
                val datePickerHeaderId = datepickerBottomsheetSymptomAddDate.getChildAt(0)
                    .resources.getIdentifier("date_picker_header","id","android")
                datepickerBottomsheetSymptomAddDate.findViewById<View>(datePickerHeaderId).visibility = View.GONE
            }

            buttonSymptomAddDate.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            if(!symptomViewModel.addSymptomDateText.value.isNullOrEmpty()){
                textViewSymptomAddDate.run {
                    text = symptomViewModel.addSymptomDateText.value
                    setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }

            if(!symptomViewModel.addSymptomDateText.value.isNullOrEmpty()){
                textViewSymptomAddDate.run {
                    text = symptomViewModel.addSymptomDateText.value
                    setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }


            if(!symptomViewModel.addSymptomDateText.value.isNullOrEmpty()){
                textViewSymptomAddDate.run {
                    text = symptomViewModel.addSymptomDateText.value
                    setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }

            timepickerSymptomAdd.run {
                setOnTimeChangedListener { timePicker, hour, minute ->
                    symptomViewModel.addSymptomTimeHour = hour
                    symptomViewModel.addSymptomTimeMinute = minute
                }
            }

            if(symptomViewModel.addSymptomTimeHour!=null&&symptomViewModel.addSymptomTimeMinute!=null){
                timepickerSymptomAdd.run {
                    hour = symptomViewModel.addSymptomTimeHour!!
                    minute = symptomViewModel.addSymptomTimeMinute!!
                }
            }

            buttonSymptomAddSelectSymptom.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.SYMPTOM_SELECT_FRAGMENT,true,null)
            }

            if (!symptomViewModel.addSymptomItemTitle.value.isNullOrEmpty()){
                layoutSymptomAddList.visibility = View.VISIBLE
                includeItemSymptomAdd.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.addSymptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = symptomViewModel.addSymptomItemTitle.value!!.trim()
                }
            }else{
                layoutSymptomAddList.visibility = View.GONE
            }


            editTextSymptomAddCustom.setOnEditorActionListener { _, actionId, keyEvent ->
                if ((actionId == EditorInfo.IME_ACTION_DONE ||
                            (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN &&
                                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) &&
                    editTextSymptomAddCustom.text.trim().isNotEmpty()
                ) {
                    layoutSymptomAddList.visibility = View.VISIBLE
                    includeItemSymptomAdd.run {
                        symptomViewModel.run {
                            addSymptomItemImgId.value = R.drawable.symptom_stethoscope
                            addSymptomItemTitle.value =
                                editTextSymptomAddCustom.text.toString().trim()
                        }
                    }
                    editTextSymptomAddCustom.text.clear()
                    editTextSymptomAddCustom.clearFocus()
                    hideKeyboard(editTextSymptomAddCustom)
                    return@setOnEditorActionListener true
                }
                false
            }
        }
        return fragmentSymptomAddBinding.root
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
                ) {}
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
            Log.d("뷰모델",symptomViewModel.addSymptomDateText.value.toString())
        }
        return customDate
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

