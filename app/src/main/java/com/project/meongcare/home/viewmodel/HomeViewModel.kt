package com.project.meongcare.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.repository.HomeRepository
import com.project.meongcare.home.model.entities.HomeProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(private val homeRepository: HomeRepository) : ViewModel() {
        private val _homeProfileResponse = MutableLiveData<HomeProfileResponse?>()
        val homeProfileResponse: LiveData<HomeProfileResponse?>
            get() = _homeProfileResponse

        fun getUserProfile(accessToken: String) {
            viewModelScope.launch {
                _homeProfileResponse.value = homeRepository.getUserProfile(accessToken)
            }
        }
    }
