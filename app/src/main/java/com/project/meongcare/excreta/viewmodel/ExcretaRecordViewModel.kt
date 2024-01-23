package com.project.meongcare.excreta.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcretaRecordViewModel
    @Inject
    constructor(
        private val excretaRepositoryImpl: ExcretaRepositoryImpl,
    ) : ViewModel() {
        private var _excretaRecordGet = MutableLiveData<ExcretaRecordGetResponse>()
        val excretaRecordGet
            get() = _excretaRecordGet

        fun getExcretaRecord(date: String) {
            viewModelScope.launch {
                val excretaRecordGetRequest =
                    ExcretaRecordGetRequest(
                        2L,
                        date,
                    )

                _excretaRecordGet.value =
                    excretaRepositoryImpl.getExcretaRecord(excretaRecordGetRequest)
            }
        }
    }
