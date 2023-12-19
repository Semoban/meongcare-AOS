package com.project.meongcare.symptom.viewmodel

import android.graphics.drawable.Drawable
import android.util.Log
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
    val listEditSymptomCheckedStatusMap = MutableLiveData<MutableMap<Int, Boolean>>()
    var isDeleteAllChecked = false
    var symptomDeleteAllImg = MutableLiveData<Int>()


    init {
        symptomList.value = mutableListOf()
        symptomItemImgId.value = R.drawable.symptom_stethoscope
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

    fun createCheckedStatusMap() {
        if (!symptomList.value.isNullOrEmpty()) {
            val currentSymptomList = symptomList.value
            val map = mutableMapOf<Int, Boolean>()
            currentSymptomList?.forEach { symptom ->
                map[symptom.symptomId] = false
            }
            listEditSymptomCheckedStatusMap.value = map
            (listEditSymptomCheckedStatusMap.value as MutableMap<Int, Boolean>).forEach { (symptomId, isSelected) ->
                Log.d("체크", "Symptom ID: $symptomId, Is Selected: $isSelected")
            }
        }
    }

    fun updateCheckedStatusMap(position: Int) {
        val currentMap = listEditSymptomCheckedStatusMap.value ?: mutableMapOf()
        val isSelected = currentMap[position] ?: false
        currentMap[position] = !isSelected
        listEditSymptomCheckedStatusMap.value = currentMap
    }

    fun setAllCheckedStatusToFalse() {
        val currentMap = listEditSymptomCheckedStatusMap.value ?: mutableMapOf()

        if (currentMap.any { it.value }) {
            currentMap.forEach { (symptomId, _) ->
                currentMap[symptomId] = false
            }
            listEditSymptomCheckedStatusMap.value = currentMap
        } else {
            currentMap.forEach { (symptomId, _) ->
                currentMap[symptomId] = true
            }
            listEditSymptomCheckedStatusMap.value = currentMap
        }
    }


    fun <K, V> Map<K, V>.allValuesEqual(): Boolean {
        val uniqueValues = values.distinct()
        return uniqueValues.size == 1
    }
}
