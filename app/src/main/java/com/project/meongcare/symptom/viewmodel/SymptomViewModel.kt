package com.project.meongcare.symptom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SymptomViewModel : ViewModel() {
    var checkedStatusList = MutableLiveData<MutableList<Boolean>>()
    var symptomList = MutableLiveData<MutableList<Symptom>>()
    var symptomDateList = MutableLiveData<MutableList<Date>>()
    var selectedDate = MutableLiveData<Date>()
    var selectDatePosition = MutableLiveData<Int>()
    init {
        checkedStatusList.value = MutableList<Boolean>(6){false}
        symptomList.value = mutableListOf(
            Symptom(1, "오전 00:30", SymptomType.WEIGHT_LOSS.symptomName, "많이 아파보임"),
            Symptom(2, "오전 01:30", SymptomType.HIGH_FEVER.symptomName, "열이 높음"),
            Symptom(3, "오전 03:15", SymptomType.COUGH.symptomName, "기침이 있음"),
            Symptom(4, "오전 08:00", SymptomType.DIARRHEA.symptomName, "설사가 있음"),
            Symptom(5, "오전 09:45", SymptomType.LOSS_OF_APPETITE.symptomName, "식욕 감퇴"),
            Symptom(6, "오전 11:20", SymptomType.ACTIVITY_DECREASE.symptomName, "활동 감소"),
            Symptom(7, "오후 02:30", SymptomType.ETC.symptomName, "기타 증상"),
        )
        selectedDate.value = Calendar.getInstance().time
        updateDateList()
    }


    private fun updateDateList() {
        val calendar = Calendar.getInstance()

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

        selectDatePosition.value = weekDates.indexOf(Calendar.getInstance().time)
        selectedDate.value = Calendar.getInstance().time

        // 날짜 목록은 현재 날짜부터 일주일 전까지의 7일간의 데이터를 포함
        symptomDateList.value = weekDates
    }

}