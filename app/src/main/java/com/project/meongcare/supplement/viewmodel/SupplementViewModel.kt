package com.project.meongcare.supplement.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.supplement.view.SupplementUtils.Companion.convertToDateToMiliSec
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.Supplement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class SupplementViewModel : ViewModel() {
    var supplementList = MutableLiveData<MutableList<Supplement>>()
    var supplementCycle = MutableLiveData<Int>()

    init {
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
}
