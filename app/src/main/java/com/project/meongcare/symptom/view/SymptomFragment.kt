package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.toolbar.HorizontalRecyclerCalendarAdapter
import com.project.meongcare.TempActivity
import com.project.meongcare.databinding.FragmentSymptomBinding
import com.tejpratapsingh.recyclercalendar.model.RecyclerCalendarConfiguration
import com.tejpratapsingh.recyclercalendar.utilities.CalendarUtils
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SymptomFragment : Fragment() {
    lateinit var fragmentSymptomBinding: FragmentSymptomBinding
    lateinit var tempActivity: TempActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomBinding = FragmentSymptomBinding.inflate(layoutInflater)
        tempActivity = activity as TempActivity

        val calendarRecyclerView: RecyclerView =
            fragmentSymptomBinding.toolbarSymptom.calendarRecyclerView
        val textViewSelectedDate: TextView =
            fragmentSymptomBinding.toolbarSymptom.textViewToolbarCalendarWeekTitleDay

        val date = Date()
        date.time = System.currentTimeMillis()

        val startCal = Calendar.getInstance()

        val endCal = Calendar.getInstance()
        endCal.time = date
        endCal.add(Calendar.MONTH, 3)

        val configuration: RecyclerCalendarConfiguration =
            RecyclerCalendarConfiguration(
                calenderViewType = RecyclerCalendarConfiguration.CalenderViewType.HORIZONTAL,
                calendarLocale = Locale.getDefault(),
                includeMonthHeader = true
            )
        configuration.weekStartOffset = RecyclerCalendarConfiguration.START_DAY_OF_WEEK.MONDAY

        textViewSelectedDate.text =
            CalendarUtils.dateStringFromFormat(
                locale = configuration.calendarLocale,
                date = date,
                format = "MM.dd EEE"
            ) ?: ""

        val calendarAdapterHorizontal: HorizontalRecyclerCalendarAdapter =
            HorizontalRecyclerCalendarAdapter(
                startDate = startCal.time,
                endDate = endCal.time,
                configuration = configuration,
                selectedDate = date,
                dateSelectListener = object : HorizontalRecyclerCalendarAdapter.OnDateSelected {
                    override fun onDateSelected(date: Date) {
                        textViewSelectedDate.text =
                            CalendarUtils.dateStringFromFormat(
                                locale = configuration.calendarLocale,
                                date = date,
                                format = "MM.dd EEE"
                            )
                                ?: ""
                    }
                }
            )

        calendarRecyclerView.adapter = calendarAdapterHorizontal

        val snapHelper = PagerSnapHelper() // Or LinearSnapHelper
        snapHelper.attachToRecyclerView(calendarRecyclerView)


        return fragmentSymptomBinding.root
    }
}
