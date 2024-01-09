package com.project.meongcare.onboarding.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DogTypeSharedViewModel : ViewModel() {
    private val _selectedDogType = MutableLiveData<String>()
    val selectedDogType
        get() = _selectedDogType

    fun setDogType(dogType: String) {
        _selectedDogType.value = dogType
    }
}
