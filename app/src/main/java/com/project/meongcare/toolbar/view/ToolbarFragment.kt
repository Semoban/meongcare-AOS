package com.project.meongcare.toolbar.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.ToolbarCalendarWeekBinding
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertToDateToLocale
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertToLocalDateToDate
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ToolbarFragment : Fragment() {
    lateinit var fragmentToolbarBinding: ToolbarCalendarWeekBinding
    lateinit var mainActivity: MainActivity
    lateinit var toolbarViewModel: ToolbarViewModel
    private lateinit var bottomSheet: LinearLayout
    lateinit var bottomSheetCloseButton: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var symptomViewModel: SymptomViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentToolbarBinding = ToolbarCalendarWeekBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        bottomSheet = mainActivity.findViewById(R.id.layout_bottomsheet_calendar)
        bottomSheetCloseButton =
            mainActivity.findViewById(R.id.button_bottomsheet_calendar_complete)

        toolbarViewModel = mainActivity.toolbarViewModel
        symptomViewModel = mainActivity.symptomViewModel

        initializeBottomSheet()
        bottomSheetEvent()
        removeDatePickerHeader()

        toolbarViewModel.run {
            selectedDate.observe(viewLifecycleOwner) {
                val localDateTime = convertToDateToLocale(it)
                Log.d("클릭4", localDateTime.toString())
                fragmentToolbarBinding.textViewToolbarCalendarWeekTitleDay.text =
                    getMonthDateDay(it)
                symptomViewModel.updateSymptomList(1, it)
            }

            dateList.observe(viewLifecycleOwner) { dateList ->
                fragmentToolbarBinding.recyclerViewToolbarCalendarWeek.run {
                    mainActivity.runOnUiThread {
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        fragmentToolbarBinding.run {
            layoutToolbarCalendarWeekTitle.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            recyclerViewToolbarCalendarWeek.run {
                adapter = ToolbarDateRecyclerViewAdapter(mainActivity)
                layoutManager = GridLayoutManager(requireContext(), 7)
            }
        }

        return fragmentToolbarBinding.root
    }

    fun removeDatePickerHeader() {
        bottomSheet.run {
            val datePickerHeaderId = bottomSheet.getChildAt(0)
                .resources.getIdentifier("date_picker_header", "id", "android")
            bottomSheet.findViewById<View>(datePickerHeaderId).visibility =
                View.GONE
        }
    }

    // Persistent BottomSheet 초기화
    private fun initializeBottomSheet() {
        // BottomSheetBehavior에 layout 설정
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

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

    private fun bottomSheetEvent() {
        bottomSheetCloseButton.setOnClickListener {
            val datePicker =
                mainActivity.findViewById<DatePicker>(R.id.datepicker_bottomsheet_calendar)
            val customDate: LocalDate =
                LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            toolbarViewModel.updateDateList(convertToLocalDateToDate(customDate))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
}
