package com.project.meongcare.supplement.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.view.SupplementUtils.Companion.convertToDateToMiliSec
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
        val localDate = convertToDateToMiliSec(date)
        SupplementRepository.searchByDogId(dogId, localDate) { supplements ->
            supplements?.let {
                val sortedSupplements =
                    it.sortedBy {
                        LocalDateTime.parse(
                            it.intakeTime,
                            DateTimeFormatter.ofPattern("HH:mm:ss"),
                        )
                    }
                supplementList.value = sortedSupplements.toMutableList()
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
