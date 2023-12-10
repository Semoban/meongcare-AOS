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
        Log.d("날짜 확인1", baseDate.toString())

        // 현재 날짜의 요일을 가져옵니다. (일요일: 1, 월요일: 2, ..., 토요일: 7)
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 현재 날짜를 기준으로 주의 시작일로 이동합니다.
        calendar.add(Calendar.DAY_OF_YEAR, 1 - currentDayOfWeek)

        // 주의 시작일부터 7일 동안의 날짜 리스트를 만듭니다.
        val weekDates = mutableListOf<Date>()
        repeat(7) {
            weekDates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        selectDatePosition.value = weekDates.indexOf(baseDate)
        selectedDate.value = baseDate

        // 날짜 목록은 현재 날짜부터 일주일 전까지의 7일간의 데이터를 포함
        dateList.value = ArrayList(weekDates)

        Log.d("날짜 확인2", dateList.value.toString())
    }

    fun getMonthDateDay(date: Date): String = SimpleDateFormat("MM.dd EE", Locale.getDefault()).format(date)
}

