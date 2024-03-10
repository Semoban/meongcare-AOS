package com.project.meongcare.medicalrecord.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.medicalrecord.model.data.repository.MedicalRecordRepositoryImpl
import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MedicalRecordViewModel
    @Inject
    constructor(
        private val medicalRecordRepositoryImpl: MedicalRecordRepositoryImpl,
    ) : ViewModel() {
        private val _medicalRecordList = MutableLiveData<Response<MedicalRecordGetResponse>>()
        val medicalRecordList: LiveData<Response<MedicalRecordGetResponse>>
            get() = _medicalRecordList

        private val _selectedDate = MutableLiveData<String>()
        val selectedDate: LiveData<String>
            get() = _selectedDate

        fun getMedicalRecordList(
            dogId: Long,
            dateTime: String,
            accessToken: String,
        ) {
            viewModelScope.launch {
                _medicalRecordList.value = medicalRecordRepositoryImpl.getMedicalRecordList(dogId, dateTime, accessToken)
            }
        }

        fun getCurrentDate(date: String) {
            _selectedDate.value = date
        }
}
