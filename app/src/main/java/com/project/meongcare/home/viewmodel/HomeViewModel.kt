package com.project.meongcare.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.repository.HomeRepository
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.HomeGetExcretaResponse
import com.project.meongcare.home.model.entities.HomeGetFeedResponse
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSupplementsResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import com.project.meongcare.home.model.entities.HomeGetWeightResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

        private val _homeDogWeight = MutableLiveData<HomeGetWeightResponse>()
        val homeDogWeight: LiveData<HomeGetWeightResponse>
            get() = _homeDogWeight

        private val _homeDogFeed = MutableLiveData<HomeGetFeedResponse>()
        val homeDogFeed: LiveData<HomeGetFeedResponse>
            get() = _homeDogFeed

        private val _homeDogSupplements = MutableLiveData<HomeGetSupplementsResponse>()
        val homeDogSupplements: LiveData<HomeGetSupplementsResponse>
            get() = _homeDogSupplements

        private val _homeDogExcreta = MutableLiveData<HomeGetExcretaResponse>()
        val homeDogExcreta: LiveData<HomeGetExcretaResponse>
            get() = _homeDogExcreta

        private val _homeDogSymptom = MutableLiveData<HomeGetSymptomResponse>()
        val homeDogSymptom: LiveData<HomeGetSymptomResponse>
            get() = _homeDogSymptom

        init {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            setSelectedDate(currentDate.format(formatter))
        }

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

        fun getDogWeight(dogId: Long, date: String, accessToken: String) {
            viewModelScope.launch {
                _homeDogWeight.value = homeRepository.getDogWeight(dogId, date, accessToken)
            }
        }

        fun getDogFeed(dogId: Long, date: String, accessToken: String) {
            viewModelScope.launch {
                _homeDogFeed.value = homeRepository.getDogFeed(dogId, date, accessToken)
            }
        }

        fun getDogSupplements(dogId: Long, date: String, accessToken: String) {
            viewModelScope.launch {
                _homeDogSupplements.value = homeRepository.getDogSupplements(dogId, date, accessToken)
            }
        }

        fun getDogExcreta(dogId: Long, dateTime: String, accessToken: String) {
            viewModelScope.launch {
                _homeDogExcreta.value = homeRepository.getDogExcreta(dogId, dateTime, accessToken)
            }
        }

        fun getDogSymptom(dogId: Long, dateTime: String, accessToken: String) {
            viewModelScope.launch {
                _homeDogSymptom.value = homeRepository.getDogSymptom(dogId, dateTime, accessToken)
            }
        }
    }
