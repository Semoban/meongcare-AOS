package com.project.meongcare.Information.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.Information.model.data.repository.ProfileRepository
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(private val profileRepository: ProfileRepository) : ViewModel() {
        private val _userProfile = MutableLiveData<GetUserProfileResponse>()
        val userProfile
            get() = _userProfile

        private val _dogList = MutableLiveData<MutableList<DogProfile>>()
        val dogList
            get() = _dogList

        fun getUserProfile(accessToken: String) {
            viewModelScope.launch {
                _userProfile.value = profileRepository.getUserProfile(accessToken)
            }
        }

        fun getDogList(accessToken: String) {
            viewModelScope.launch {
                _dogList.value = profileRepository.getDogList(accessToken)
            }
        }
    }