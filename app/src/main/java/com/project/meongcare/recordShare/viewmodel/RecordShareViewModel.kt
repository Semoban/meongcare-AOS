package com.project.meongcare.recordShare.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.recordShare.model.entities.GetDogListResponse
import com.project.meongcare.recordShare.model.data.repository.RecordShareRepository
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
