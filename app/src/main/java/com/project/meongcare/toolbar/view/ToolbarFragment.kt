package com.project.meongcare.toolbar.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.ToolbarCalendarWeekBinding
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertToDateToLocale
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertToLocalDateToDate
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.time.LocalDate

class ToolbarFragment : Fragment() {
    lateinit var fragmentToolbarBinding: ToolbarCalendarWeekBinding
    lateinit var mainActivity: MainActivity
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var symptomViewModel: SymptomViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentToolbarBinding = ToolbarCalendarWeekBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        toolbarViewModel = mainActivity.toolbarViewModel
        symptomViewModel = mainActivity.symptomViewModel

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
                showCalendarBottomSheet()
            }
            recyclerViewToolbarCalendarWeek.run {
                adapter = ToolbarDateRecyclerViewAdapter(mainActivity)
                layoutManager = GridLayoutManager(requireContext(), 7)
            }
        }

        return fragmentToolbarBinding.root
    }

    private fun showCalendarBottomSheet() {
        val bottomSheetDialogFragment =
            CalendarBottomSheetDialogFragment().apply {
                onDateSelectedListener =
                    object : CalendarBottomSheetDialogFragment.OnDateSelectedListener {
                        override fun onDateSelected(date: LocalDate) {
                            toolbarViewModel.updateDateList(convertToLocalDateToDate(date))
                        }
                    }
            }
        bottomSheetDialogFragment.show(parentFragmentManager, "CalendarBottomSheetDialogFragment")
    }
}
