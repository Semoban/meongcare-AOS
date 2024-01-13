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
import kotlinx.coroutines.withContext
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
    var addSymptomCode = MutableLiveData<Int?>()
    var deleteSymptomCode = MutableLiveData<Int?>()
    var patchSymptomIsSuccess = MutableLiveData<Boolean>()
    var symptomIdList = MutableLiveData<MutableList<Int>>()
    var symptomIdListAllCheck = MutableLiveData<Boolean>()

    init {
        symptomList.value = mutableListOf()
        symptomIdList.value = mutableListOf()
        symptomItemImgId.value = R.drawable.symptom_etc_record
        textViewNoDataVisibility.value = false
        symptomIdListAllCheck.value = false
        patchSymptomIsSuccess.value = false
    }

    fun getSymptomList(
        dogId: Int,
        date: Date,
    ) {
        val localDate = convertToDateToMiliSec(date)
        viewModelScope.launch {
            val symptoms = repository.getSymptomByDogId(dogId, localDate)
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
            deleteSymptomCode.value = repository.deleteSymptom(symptomIds)
        }
    }

    fun patchSymptom(toEditSymptom: ToEditSymptom) {
        viewModelScope.launch {
            val patch = repository.patchSymptom(toEditSymptom)
            patch.onSuccess {
                patchSymptomIsSuccess.value = true
                Log.d("이상증상 수정 Api 통신 성공", it.toString())
            }.onFailure {
                patchSymptomIsSuccess.value = false
                Log.d("이상증상 수정 Api 통신 에러", it.toString())
            }
        }
    }

    fun updateSymptomData(position: Int) {
        infoSymptomData.value = symptomList.value?.get(position)
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
}
