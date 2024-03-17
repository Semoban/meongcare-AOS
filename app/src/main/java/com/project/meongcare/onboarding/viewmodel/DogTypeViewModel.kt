package com.project.meongcare.onboarding.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.meongcare.onboarding.model.data.repository.DogTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogTypeViewModel
    @Inject
    constructor(
        private val dogTypeRepository: DogTypeRepository,
    ) : ViewModel() {
        private val _dogTypeList = MutableLiveData<List<String>>()
        val dogTypeList
            get() = _dogTypeList

        fun searchDogType(query: String) {
            _dogTypeList.value = dogTypeRepository.searchTypes(query)
        }
    }
