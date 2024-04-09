package com.project.meongcare.medicalRecord.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.login.model.entities.ReissueResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
        private val loginRepository: LoginRepository,
    ) : ViewModel() {
        val accessTokenPreferencesLiveData = userPreferences.accessToken.asLiveData()
        val refreshTokenPreferencesLiveData = userPreferences.refreshToken.asLiveData()
        val providerPreferencesLiveData = userPreferences.provider.asLiveData()
        val emailPreferencesLiveData = userPreferences.email.asLiveData()

        private val _reissueResponse = MutableLiveData<Response<ReissueResponse>>()
        val reissueResponse: LiveData<Response<ReissueResponse>>
            get() = _reissueResponse

        fun getNewAccessToken(refreshToken: String) {
            viewModelScope.launch {
                _reissueResponse.value = loginRepository.getNewAccessToken(refreshToken)
            }
        }

        fun setAccessToken(accessToken: String?) {
            viewModelScope.launch {
                userPreferences.editAccessToken(accessToken)
            }
        }

        fun setProvider(provider: String?) {
            viewModelScope.launch {
                userPreferences.editProvider(provider)
            }
        }

        fun setRefreshToken(refreshToken: String?) {
            viewModelScope.launch {
                userPreferences.editRefreshToken(refreshToken)
            }
        }

        fun setIsFirstLogin(isFirstLogin: Boolean?) {
            viewModelScope.launch {
                userPreferences.editIsFirstLogin(isFirstLogin)
            }
        }

        fun setEmail(email: String?) {
            viewModelScope.launch {
                userPreferences.editEmail(email)
            }
        }
    }
