package com.project.meongcare.symptom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.project.meongcare.databinding.FragmentSymptomBinding
import java.time.LocalDate
import java.time.YearMonth

class SymptomFragment : Fragment() {
    lateinit var fragmentSymptomBinding: FragmentSymptomBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSymptomBinding = FragmentSymptomBinding.inflate(layoutInflater)

        fragmentSymptomBinding.run {
            val currentDate = LocalDate.now()
            val currentMonth = YearMonth.now()
            val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
            val endDate = currentMonth.plusMonths(100).atEndOfMonth()  // Adjust as needed
            val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
            toolbarSymptom.weekCalendarView.run {
                setup(startDate, endDate, firstDayOfWeek)
                scrollToWeek(currentDate)
            }
        }
        return fragmentSymptomBinding.root
    }
}
