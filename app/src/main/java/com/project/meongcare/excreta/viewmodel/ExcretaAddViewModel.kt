package com.project.meongcare.excreta.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExcretaAddViewModel
    @Inject
    constructor() : ViewModel() {
        private var _excretaDate = MutableLiveData<String>()
        val excretaDate
            get() = _excretaDate

        fun getExcretaDate(date: String) {
            _excretaDate.value = date
        }
    }
