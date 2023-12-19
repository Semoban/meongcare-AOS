package com.project.meongcare.symptom.viewmodel

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.R
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.view.SymptomUtils
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class SymptomViewModel : ViewModel() {
    var checkedStatusList = MutableLiveData<MutableList<Boolean>>()
    var symptomList = MutableLiveData<MutableList<Symptom>>()
    var symptomDateText = MutableLiveData<String?>()
    var symptomTimeHour: Int? = null
    var symptomTimeMinute: Int? = null
    var symptomItemImgId = MutableLiveData<Int>()
    var symptomItemTitle = MutableLiveData<String?>()
    var symptomItemVisibility = MutableLiveData<Int>()
    var selectCheckedImg = MutableLiveData<ImageView>()
    var textViewNoDataVisibility = MutableLiveData<Boolean>()
    var infoSymptomData = MutableLiveData<Symptom>()
    var isEditSymptom = false

    init {
        symptomList.value = mutableListOf()
        symptomItemImgId.value = R.drawable.symptom_stethoscope
        checkedStatusList.value = MutableList<Boolean>(6) { false }
        textViewNoDataVisibility.value = false
    }

    fun updateSymptomList(
        dogId: Int,
        date: Date
    ) {
        val localDate = convertToDateToMiliSec(date)
        SymptomRepository.searchByDogId(dogId, localDate) { symptoms ->
            symptoms?.let {
                val sortedSymptoms = it.sortedBy {
                    LocalDateTime.parse(it.dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                }

                symptomList.value = sortedSymptoms.toMutableList()
            }
        }
    }

    fun updateSymptomData(position: Int) {
        infoSymptomData.value = symptomList.value?.get(position)
    }

    fun updateSymptomDataAll() {
        val symptom = infoSymptomData.value
        if (symptom != null) {
            symptomDateText.value = symptom.dateTime
            symptomItemImgId.value = SymptomUtils.getSymptomImg(symptom)
            symptomItemTitle.value = symptom.note
            symptomItemVisibility.value = View.VISIBLE
        }
    }

    fun convertToDateToMiliSec(date: Date): String {
        // Date를 Instant로 변환
        val instant: Instant = date.toInstant()

        // Instant를 ZoneId를 사용하여 LocalDateTime으로 변환
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return localDateTime.format(formatter)
    }

    fun clearLiveData() {
        symptomDateText.value = null
        symptomTimeHour = null
        symptomTimeMinute = null
        symptomItemImgId.value = R.drawable.symptom_stethoscope
        symptomItemTitle.value = null
        symptomItemVisibility.value = View.GONE
        isEditSymptom = false
    }

    fun updateSymptomDate(date: LocalDate) {
        if(isEditSymptom){
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
            symptomDateText.value = date.format(formatter)
        }else{
            symptomDateText.value = date.toString()
        }
    }
}
