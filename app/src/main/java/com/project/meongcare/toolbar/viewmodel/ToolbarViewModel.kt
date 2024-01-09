package com.project.meongcare.toolbar.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ToolbarViewModel : ViewModel() {
    var dateList = MutableLiveData<MutableList<Date>>()
    var selectedDate = MutableLiveData<Date>()
    var selectDatePosition = MutableLiveData<Int>()

    init {
        selectedDate.value = Calendar.getInstance().time
        updateDateList(Calendar.getInstance().time)
    }

    fun updateDateList(baseDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = baseDate

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        calendar.add(Calendar.DAY_OF_YEAR, 1 - currentDayOfWeek)

        val weekDates = mutableListOf<Date>()
        repeat(7) {
            weekDates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        selectDatePosition.value = weekDates.indexOf(baseDate)
        selectedDate.value = baseDate

        dateList.value = ArrayList(weekDates)
    }

    fun getMonthDateDay(date: Date): String = SimpleDateFormat("MM.dd EE", Locale.getDefault()).format(date)
}

