package com.project.meongcare.medicalrecord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.meongcare.login.model.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
    ) : ViewModel() {
        val accessTokenPreferencesLiveData = userPreferences.accessToken.asLiveData()
    }
