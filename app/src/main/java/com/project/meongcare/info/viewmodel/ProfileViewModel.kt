package com.project.meongcare.info.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import com.project.meongcare.info.model.data.repository.ProfileRepository
import com.project.meongcare.info.model.entities.DogPutRequest
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.info.model.entities.ProfilePatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(private val profileRepository: ProfileRepository) : ViewModel() {
        private val _userProfile = MutableLiveData<Response<GetUserProfileResponse>?>()
        val userProfile
            get() = _userProfile

        private val _dogList = MutableLiveData<Response<GetDogListResponse>?>()
        val dogList
            get() = _dogList

        private val _dogInfo = MutableLiveData<Response<GetDogInfoResponse>?>()
        val dogInfo
            get() = _dogInfo

        private val _dogDeleteResponse = MutableLiveData<Int>()
        val dogDeleteResponse
            get() = _dogDeleteResponse

        private val _dogPutResponse = MutableLiveData<Int>()
        val dogPutResponse
            get() = _dogPutResponse

        private val _postDogWeightResponse = MutableLiveData<Int>()
        val postDogWeightResponse: LiveData<Int>
            get() = _postDogWeightResponse

        private val _patchDogWeightResponse = MutableLiveData<Int>()
        val patchDogWeightResponse: LiveData<Int>
            get() = _patchDogWeightResponse

        private val _logoutResponse = MutableLiveData<Int>()
        val logoutResponse
            get() = _logoutResponse

        private val _userDeleteResponse = MutableLiveData<Int>()
        val userDeleteResponse
            get() = _userDeleteResponse

        private val _patchPushResponse = MutableLiveData<Int>()
        val patchPushResponse
            get() = _patchPushResponse

        private val _patchProfileResponse = MutableLiveData<Int>()
        val patchProfileResponse
            get() = _patchProfileResponse

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
            dogPutRequest: DogPutRequest,
        ) {
            viewModelScope.launch {
                _dogPutResponse.value = profileRepository.putDogInfo(dogId, accessToken, dogPutRequest)
            }
        }

        fun postDogWeight(
            accessToken: String,
            dogWeightBody: WeightPostRequest,
        ) {
            viewModelScope.launch {
                _postDogWeightResponse.value = profileRepository.postDogWeight(accessToken, dogWeightBody)
            }
        }

        fun patchDogWeight(
            dogId: Long,
            kg: Double,
            date: String,
            accessToken: String,
        ) {
            viewModelScope.launch {
                _patchDogWeightResponse.value = profileRepository.patchDogWeight(dogId, kg, date, accessToken)
            }
        }

        fun logoutUser(refreshToken: String) {
            viewModelScope.launch {
                _logoutResponse.value = profileRepository.logoutUser(refreshToken)
            }
        }

        fun deleteUser(accessToken: String) {
            viewModelScope.launch {
                _userDeleteResponse.value = profileRepository.deleteUser(accessToken)
            }
        }

        fun patchPushAgreement(
            pushAgreement: Boolean,
            accessToken: String,
        ) {
            viewModelScope.launch {
                _patchPushResponse.value = profileRepository.patchPushAgreement(pushAgreement, accessToken)
            }
        }

        fun patchProfileImage(
            accessToken: String,
            profilePatchRequest: ProfilePatchRequest,
        ) {
            viewModelScope.launch {
                _patchProfileResponse.value = profileRepository.patchProfileImage(accessToken, profilePatchRequest)
            }
        }
    }
