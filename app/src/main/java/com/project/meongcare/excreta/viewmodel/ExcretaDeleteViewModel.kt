package com.project.meongcare.excreta.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcretaDeleteViewModel
    @Inject
    constructor(
        private val excretaRepositoryImpl: ExcretaRepositoryImpl,
    ): ViewModel() {
        private var _excretaDeleted = MutableLiveData<Int>()
        val excretaDeleted
            get() = _excretaDeleted

        fun deleteExcreta(excretaIds: IntArray) {
            viewModelScope.launch {
                _excretaDeleted.value =
                    excretaRepositoryImpl.deleteExcreta(excretaIds)
            }
        }
    }
