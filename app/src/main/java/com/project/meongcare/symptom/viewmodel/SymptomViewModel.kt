package com.project.meongcare.symptom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import java.util.Calendar
import java.util.Date

class SymptomViewModel : ViewModel() {
    var checkedStatusList = MutableLiveData<MutableList<Boolean>>()
    var symptomList = MutableLiveData<MutableList<Symptom>>()
    init {
        checkedStatusList.value = MutableList<Boolean>(6) { false }
        symptomList.value =
            mutableListOf(
                Symptom(1, "오전 00:30", SymptomType.WEIGHT_LOSS.symptomName, "많이 아파보임"),
                Symptom(2, "오전 01:30", SymptomType.HIGH_FEVER.symptomName, "열이 높음"),
                Symptom(3, "오전 03:15", SymptomType.COUGH.symptomName, "기침이 있음"),
                Symptom(4, "오전 08:00", SymptomType.DIARRHEA.symptomName, "설사가 있음"),
                Symptom(5, "오전 09:45", SymptomType.LOSS_OF_APPETITE.symptomName, "식욕 감퇴"),
                Symptom(6, "오전 11:20", SymptomType.ACTIVITY_DECREASE.symptomName, "활동 감소"),
                Symptom(7, "오후 02:30", SymptomType.ETC.symptomName, "기타 증상"),
            )
    }
}
