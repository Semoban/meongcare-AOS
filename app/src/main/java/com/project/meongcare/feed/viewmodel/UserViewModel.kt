package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.login.model.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
    ) : ViewModel() {
        private var _accessToken = MutableLiveData<String>()
        val accessToken
            get() = _accessToken
        fun fetchAccessToken() {
            viewModelScope.launch {
                userPreferences.accessToken.collect { accessToken ->
                    _accessToken.value = accessToken
                }
            }
        }
    }
