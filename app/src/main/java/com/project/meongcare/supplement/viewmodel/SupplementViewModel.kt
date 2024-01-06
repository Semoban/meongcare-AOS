package com.project.meongcare.supplement.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertToDateToDate
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertToDateToDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class SupplementViewModel : ViewModel() {
    var supplementList = MutableLiveData<MutableList<Supplement>>()
    var intakeTimeList = MutableLiveData<MutableList<IntakeInfo>>()
    var intakeTimeUnit = MutableLiveData<String>()
    val mgButtonSelected = MutableLiveData<Boolean>(true)
    val scoopButtonSelected = MutableLiveData<Boolean>(false)
    val jungButtonSelected = MutableLiveData<Boolean>(false)
    var supplementCycle = MutableLiveData<Int>()

    init {
        intakeTimeList.value = mutableListOf()
        supplementList.value = mutableListOf()
    }

    fun updateSupplementList(
        dogId: Int,
        date: Date,
    ) {
        val localDate = convertToDateToDate(date)
        Log.d("Supplement",localDate.toString())
        SupplementRepository.searchByDogId(dogId, localDate) { supplements ->
            if(!supplements.isNullOrEmpty()) {
                Log.d("Supplement", supplements.toString())
                supplementList.value = supplements!!.sortedBy { i -> i.intakeTime }.toMutableList()
                Log.d("Supplement 3", supplementList.value.toString())
            }
        }
    }

    fun addIntakeInfoList(
        intakeInfo: IntakeInfo
    ) {
        val currentList = intakeTimeList.value?.toMutableList() ?: mutableListOf()

        if (currentList.none { i -> i.intakeTime == intakeInfo.intakeTime }){
            currentList.add(intakeInfo)
        }

        intakeTimeList.value = currentList.distinct().sortedBy { intakeInfo -> intakeInfo.intakeTime } as MutableList<IntakeInfo>
    }


    fun updateButtonStates(selectedButton: MutableLiveData<Boolean>) {
        mgButtonSelected.value = selectedButton == mgButtonSelected
        scoopButtonSelected.value = selectedButton == scoopButtonSelected
        jungButtonSelected.value = selectedButton == jungButtonSelected
    }

    fun onMgButtonClick() {
        intakeTimeUnit.value = "mg"
        updateButtonStates(mgButtonSelected)
    }

    fun onScoopButtonClick() {
        intakeTimeUnit.value = "스쿱"
        updateButtonStates(scoopButtonSelected)
    }

    fun onJungButtonClick() {
        intakeTimeUnit.value = "정"
        updateButtonStates(jungButtonSelected)
    }

    fun removeIntakeTimeListItem(indexToRemove: Int) {
        val currentList = intakeTimeList.value?.toMutableList() ?: mutableListOf()

        if (indexToRemove >= 0 && indexToRemove < currentList.size) {
            currentList.removeAt(indexToRemove)
            intakeTimeList.value = currentList
        }
    }
}
