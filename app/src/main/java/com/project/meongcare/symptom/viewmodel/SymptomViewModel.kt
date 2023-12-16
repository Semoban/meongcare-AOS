package com.project.meongcare.symptom.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.R
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SymptomViewModel : ViewModel() {
    var checkedStatusList = MutableLiveData<MutableList<Boolean>>()
    var symptomList = MutableLiveData<MutableList<Symptom>>()
    var addSymptomDateText = MutableLiveData<String?>()
    var addSymptomTimeHour: Int? = null
    var addSymptomTimeMinute: Int? = null
    var addSymptomItemImgId = MutableLiveData<Int>()
    var addSymptomItemTitle = MutableLiveData<String?>()
    var addSymptomItemVisibility = MutableLiveData<Int>()
    var selectCheckedImg = MutableLiveData<ImageView>()
    var textViewNoDataVisibility = MutableLiveData<Boolean>()
    var infoSymptomData = MutableLiveData<Symptom>()

    init {
        symptomList.value = mutableListOf()
        addSymptomItemImgId.value = R.drawable.symptom_stethoscope
        checkedStatusList.value = MutableList<Boolean>(6) { false }
        textViewNoDataVisibility.value = false
    }

    fun updateSymptomList(dogId: Int, date: Date) {
        val localDate = convertToDateToMiliSec(date)
        SymptomRepository.searchByDogId(dogId, localDate) {
            symptomList.value = it as MutableList<Symptom>
        }
    }

    fun updateSymptomData(position:Int){
        infoSymptomData.value = symptomList.value?.get(position)
        Log.d("증상확인",infoSymptomData.value.toString())
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
        addSymptomDateText.value = null
        addSymptomTimeHour = null
        addSymptomTimeMinute = null
        addSymptomItemImgId.value = R.drawable.symptom_stethoscope
        addSymptomDateText.value = null
        addSymptomItemTitle.value = null
        addSymptomItemVisibility.value = View.GONE
    }


}
