package com.project.meongcare.toolbar.viewmodel

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

    // 주간 스트립에서 날짜 클릭 시 호출 — 이미 선택된 날짜와 같은 날이면 무시한다
    fun selectDateAt(position: Int) {
        val newDate = dateList.value?.getOrNull(position) ?: return
        val currentDate = selectedDate.value
        if (currentDate != null && toDayString(currentDate) == toDayString(newDate)) return

        selectDatePosition.value = position
        selectedDate.value = newDate
    }

    fun moveWeek(days: Int) {
        val baseDate = selectedDate.value ?: return
        val calendar = Calendar.getInstance()
        calendar.time = baseDate
        calendar.add(Calendar.DAY_OF_YEAR, days)
        updateDateList(calendar.time)
    }

    private fun toDayString(date: Date): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}
