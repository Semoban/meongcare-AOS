package com.project.meongcare.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.model.entities.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(private val loginRepository: LoginRepository) : ViewModel() {
        private val _loginResponse = MutableLiveData<LoginResponse?>()
        val loginResponse: LiveData<LoginResponse?>
            get() = _loginResponse

        fun postLoginInfo(loginRequest: LoginRequest) {
            viewModelScope.launch {
                _loginResponse.value = loginRepository.postLoginInfo(loginRequest)
            }
        }
    }
