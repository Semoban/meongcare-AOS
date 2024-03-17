package com.project.meongcare.symptom.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.R
import com.project.meongcare.home.model.data.local.DogPreferences
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.view.GlobalApplication
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertToDateToMiliSec
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SymptomViewModel
    @Inject
    constructor(private val repository: SymptomRepository) : ViewModel() {
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
        var patchSymptomIsSuccess = MutableLiveData<Boolean?>()
        var symptomIdList = MutableLiveData<MutableList<Int>>()
        var symptomIdListAllCheck = MutableLiveData<Boolean>()
        var dogName = MutableLiveData<String>()

        init {
            symptomList.value = mutableListOf()
            symptomIdList.value = mutableListOf()
            symptomItemImgId.value = R.drawable.symptom_etc_record
            textViewNoDataVisibility.value = false
            symptomIdListAllCheck.value = false
            patchSymptomIsSuccess.value = null
            getDogName()
        }

        fun getSymptomList(date: Date) {
            val localDate = convertToDateToMiliSec(date)
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()
                Log.d("Symptom get Api accessToken", "$accessToken")
                Log.d("Symptom get Api dogId", "$dogId")
                val symptoms = repository.getSymptomByDogId(accessToken, dogId, localDate)
                symptoms.onSuccess {
                    symptomList.value = it.records.sortedBy { s -> s.dateTime }.toMutableList()
                    Log.d("Symptom get Api 통신 성공", symptomList.value.toString())
                }.onFailure {
                    Log.d("Symptom get Api 통신 에러", it.toString())
                }
            }
        }

        fun addSymptomData(
            addItemName: String,
            addItemTitle: String,
            dateTimeString: String,
        ) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()
                Log.d("Symptom add Api accessToken", "$accessToken")
                Log.d("Symptom add Api dogId", "$dogId")
                val toAddSymptom =
                    ToAddSymptom(dogId!!.toInt(), addItemName, addItemTitle, dateTimeString)
                Log.d("Symptom add Api toAddSymptom", "$toAddSymptom")
                addSymptomCode.value = repository.addSymptom(accessToken, toAddSymptom)
                Log.d("Symptom add Api addSymptomCode", "${addSymptomCode.value}")
            }
        }

        fun deleteSymptom(symptomIds: IntArray) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                deleteSymptomCode.value = repository.deleteSymptom(accessToken, symptomIds)
            }
        }

        fun patchSymptom(toEditSymptom: ToEditSymptom) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val patch = repository.patchSymptom(accessToken, toEditSymptom)
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

        fun updateSymptomDate(
            date: LocalDate,
            isEditSymptom: Boolean,
        ) {
            if (isEditSymptom) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
                symptomDateText.value = date.format(formatter)
            } else {
                symptomDateText.value = date.toString()
            }
        }

        fun getDogName() {
            viewModelScope.launch {
                dogName.value = DogPreferences(GlobalApplication.applicationContext()).dogName.first()
            }
        }
}
