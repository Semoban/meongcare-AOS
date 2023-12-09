package com.project.meongcare.toolbar.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.ToolbarCalendarWeekBinding
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ToolbarFragment : Fragment() {
    lateinit var fragmentToolbarBinding: ToolbarCalendarWeekBinding
    lateinit var mainActivity: MainActivity
    lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentToolbarBinding = ToolbarCalendarWeekBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        toolbarViewModel = ViewModelProvider(mainActivity)[ToolbarViewModel::class.java]

        toolbarViewModel.run {
            selectedDate.observe(viewLifecycleOwner) {
                val localDateTime = changeDateToLocale(it)
                Log.d("클릭4", localDateTime.toString())

                // fragment바인딩이름.include시 설정한 toolbar이름.textViewToolbarCalendarWeekTitleDay.text
                fragmentToolbarBinding.textViewToolbarCalendarWeekTitleDay.text =
                    getMonthDateDay(it)
            }

            dateList.observe(viewLifecycleOwner) { dateList ->
                // fragment바인딩이름.include시 설정한 toolbar이름.recyclerViewToolbarCalendarWeek.run {
                fragmentToolbarBinding.recyclerViewToolbarCalendarWeek.run {
                    mainActivity.runOnUiThread {
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        fragmentToolbarBinding.run {
            recyclerViewToolbarCalendarWeek.run {
                adapter = ToolbarDateRecyclerViewAdapter(mainActivity)
                layoutManager = GridLayoutManager(requireContext(), 7)
            }
        }

        return fragmentToolbarBinding.root
    }

    fun changeDateToLocale(date: Date): LocalDateTime {
        // Date를 Instant로 변환
        val instant: Instant = date.toInstant()

        // Instant를 ZoneId를 사용하여 LocalDateTime으로 변환
        val localDateTime: LocalDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return localDateTime
    }

}