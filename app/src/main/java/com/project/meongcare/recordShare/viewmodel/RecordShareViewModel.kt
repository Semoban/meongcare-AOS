package com.project.meongcare.recordShare.viewmodel

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
import com.project.meongcare.recordShare.model.data.repository.RecordShareRepository
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecordShareViewModel
    @Inject
    constructor(private val recordShareRepository: RecordShareRepository) : ViewModel() {
        private val _dogList = MutableLiveData<Response<GetDogListResponse>?>()
        val dogList
            get() = _dogList

        fun getDogList(accessToken: String) {
            viewModelScope.launch {
                _dogList.value = recordShareRepository.getDogList(accessToken)
            }
        }
    }
