package com.project.meongcare.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.repository.HomeRepository
import com.project.meongcare.home.model.entities.DogProfile
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

        private val _homeDogList = MutableLiveData<MutableList<DogProfile>?>()
        val homeDogList: LiveData<MutableList<DogProfile>?>
            get() = _homeDogList

        private val _homeSelectedDogPos = MutableLiveData<Int>()
        val homeSelectedDogPos: LiveData<Int>
            get() = _homeSelectedDogPos

        private val _homeSelectedDogId = MutableLiveData<Long>()
        val homeSelectedDogId: LiveData<Long>
            get() = _homeSelectedDogId
        fun getUserProfile(accessToken: String) {
            viewModelScope.launch {
                _homeProfileResponse.value = homeRepository.getUserProfile(accessToken)
            }
        }

        fun setSelectedDate(str: String) {
            _homeSelectedDate.value = str
        }

        fun getDogList(accessToken: String) {
            viewModelScope.launch {
                _homeDogList.value = homeRepository.getDogList(accessToken)
            }
        }

        fun setSelectedDogId(id: Long) {
            _homeSelectedDogId.value = id
        }

        fun setSelectedDogPos(pos: Int) {
            _homeSelectedDogPos.value = pos
            Log.d("homeViewModel", "currentPos : $pos")
        }
    }
