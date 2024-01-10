package com.project.meongcare.symptom.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.R
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import com.project.meongcare.symptom.utils.SymptomUtils
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertToDateToMiliSec
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class SymptomViewModel(private val repository: SymptomRepository) : ViewModel() {
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
    val listEditSymptomCheckedStatusMap = MutableLiveData<MutableMap<Int, Boolean>>()
    var addSymptomCode = MutableLiveData<Int?>()

    init {
        symptomList.value = mutableListOf()
        symptomItemImgId.value = R.drawable.symptom_etc_record
        textViewNoDataVisibility.value = false
    }

    fun getSymptomList(
        dogId: Int,
        date: Date,
    ) {
        val localDate = convertToDateToMiliSec(date)
        viewModelScope.launch {
            val symptoms = repository.getSupplementByDogId(dogId, localDate)
            symptoms.onSuccess {
                symptomList.value = it.records.sortedBy { s -> s.dateTime }.toMutableList()
                Log.d("Symptom get Api 통신 성공", symptomList.value.toString())
            }.onFailure {
                Log.d("Symptom get Api 통신 에러", it.toString())
            }
        }
    }

    fun addSymptomData(toAddSymptom: ToAddSymptom) {
        viewModelScope.launch {
            addSymptomCode.value = repository.addSymptom(toAddSymptom)
        }
    }

    fun deleteSymptom(symptomIds: IntArray) {
        viewModelScope.launch {
            val delete = repository.deleteSymptom(symptomIds)
            delete.onSuccess {
                Log.d("이상증상 삭제 Api 통신 성공", it.toString())
            }.onFailure {
                Log.d("이상증상 삭제 Api 통신 에러", it.toString())
            }
        }
    }

    fun patchSymptom(toEditSymptom: ToEditSymptom) {
        viewModelScope.launch {
            val delete = repository.patchSymptom(toEditSymptom)
            delete.onSuccess {
                Log.d("이상증상 수정 Api 통신 성공", it.toString())
            }.onFailure {
                Log.d("이상증상 수정 Api 통신 에러", it.toString())
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

    fun clearLiveData() {
        symptomDateText.value = null
        symptomTimeHour = null
        symptomTimeMinute = null
        symptomItemImgId.value = R.drawable.symptom_etc_record
        symptomItemTitle.value = null
        symptomItemVisibility.value = View.GONE
    }

    fun updateSymptomDate(date: LocalDate, isEditSymptom: Boolean) {
        if (isEditSymptom) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
            symptomDateText.value = date.format(formatter)
        } else {
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

    fun updateAllCheckedStatus() {
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

    fun readDogData() {

    }

}
