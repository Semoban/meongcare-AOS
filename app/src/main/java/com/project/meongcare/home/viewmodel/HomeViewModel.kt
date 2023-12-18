package com.project.meongcare.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.repository.HomeRepository
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(private val homeRepository: HomeRepository) : ViewModel() {
        private val _homeProfileResponse = MutableLiveData<HomeGetProfileResponse?>()
        val homeProfileResponse: LiveData<HomeGetProfileResponse?>
            get() = _homeProfileResponse

        private val _homeSelectedDate = MutableLiveData<String>()
        val homeSelectedDate: LiveData<String>
            get() = _homeSelectedDate

        fun getUserProfile(accessToken: String) {
            viewModelScope.launch {
                _homeProfileResponse.value = homeRepository.getUserProfile(accessToken)
            }
        }

        fun setSelectedDate(str: String) {
            _homeSelectedDate.value = str
        }
    }
