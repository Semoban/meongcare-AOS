package com.project.meongcare.medicalrecord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.meongcare.home.model.data.local.DogPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
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
