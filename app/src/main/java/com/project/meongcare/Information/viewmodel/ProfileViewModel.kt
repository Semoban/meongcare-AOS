package com.project.meongcare.Information.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.Information.model.data.repository.ProfileRepository
import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

        private val _dogInfo = MutableLiveData<GetDogInfoResponse>()
        val dogInfo
            get() = _dogInfo

        private val _dogDeleteResponse = MutableLiveData<Int>()
        val dogDeleteResponse
            get() = _dogDeleteResponse

        private val _dogPutResponse = MutableLiveData<Int>()
        val dogPutResponse
            get() = _dogPutResponse

        private val _logoutResponse = MutableLiveData<Int>()
        val logoutResponse
            get() = _logoutResponse

        private val _dogProfile = MutableLiveData<Uri>()
        val dogProfile
            get() = _dogProfile

        private val _dogBirth = MutableLiveData<String>()
        val dogBirth
            get() = _dogBirth

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

        fun getDogInfo(
            dogId: Long,
            accessToken: String,
        ) {
            viewModelScope.launch {
                _dogInfo.value = profileRepository.getdogInfo(dogId, accessToken)
            }
        }

        fun deleteDog(
            dogId: Long,
            accessToken: String,
        ) {
            viewModelScope.launch {
                _dogDeleteResponse.value = profileRepository.deleteDog(dogId, accessToken)
            }
        }

        fun setDogProfile(uri: Uri) {
            _dogProfile.value = uri
        }

        fun setDogBirth(birth: String) {
            _dogBirth.value = birth
        }

        fun putDogInfo(
            dogId: Long,
            accessToken: String,
            file: MultipartBody.Part,
            dto: RequestBody,
        ) {
            viewModelScope.launch {
                _dogPutResponse.value = profileRepository.putDogInfo(dogId, accessToken, file, dto)
            }
        }

        fun logoutUser(refreshToken: String) {
            viewModelScope.launch {
                _logoutResponse.value = profileRepository.logoutUser(refreshToken)
            }
        }
    }
