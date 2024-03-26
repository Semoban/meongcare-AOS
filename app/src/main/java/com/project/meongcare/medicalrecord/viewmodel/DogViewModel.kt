package com.project.meongcare.medicalrecord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.local.DogPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogViewModel
    @Inject
    constructor(
        private val dogPreferences: DogPreferences,
    ) : ViewModel() {
        val dogNamePreferencesLiveData = dogPreferences.dogName.asLiveData()
        val dogIdPreferencesLiveData = dogPreferences.dogId.asLiveData()
        val dogWeightPreferencesLiveData = dogPreferences.dogWeight.asLiveData()

        fun setDogName(dogName: String?) {
            viewModelScope.launch {
                dogPreferences.editDogName(dogName)
            }
        }

        fun setDogId(dogId: Long?) {
            viewModelScope.launch {
                dogPreferences.editDogId(dogId)
            }
        }

        fun setDogWeight(dogWeight: Double?) {
            viewModelScope.launch {
                dogPreferences.editDogWeight(dogWeight)
            }
        }
    }
