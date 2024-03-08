package com.project.meongcare.medicalrecord.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.local.DogPreferences
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.medicalrecord.model.data.repository.MedicalRecordRepositoryImpl
import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import com.project.meongcare.medicalrecord.model.entities.MedicalRecordListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DogViewModel
    @Inject
    constructor(
        private val dogPreferences: DogPreferences,
    ) : ViewModel() {
        val dogNamePreferencesLiveData = dogPreferences.dogName.asLiveData()
        val dogIdPreferencesLiveData = dogPreferences.dogId.asLiveData()
}
