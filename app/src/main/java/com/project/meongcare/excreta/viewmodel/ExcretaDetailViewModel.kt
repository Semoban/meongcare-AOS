package com.project.meongcare.excreta.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcretaDetailViewModel
    @Inject
    constructor(
        private val excretaRepositoryImpl: ExcretaRepositoryImpl,
    ) : ViewModel() {
        private var _excretaDetailGet = MutableLiveData<ExcretaDetailGetResponse>()
        val excretaDetailGet
            get() = _excretaDetailGet

        fun getExcretaDetail(
            accessToken: String,
            excretaId: Long,
        ) {
            viewModelScope.launch {
                _excretaDetailGet.value =
                    excretaRepositoryImpl.getExcretaDetail(accessToken, excretaId)
            }
        }
    }
