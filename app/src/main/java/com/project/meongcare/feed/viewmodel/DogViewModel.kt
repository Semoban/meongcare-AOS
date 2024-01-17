package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.local.DogPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogViewModel
    @Inject
    constructor(
        private val dogPreferences: DogPreferences
    ): ViewModel() {
        private var _dogId = MutableLiveData<Long>()
        val dogId
            get() = _dogId

        private var _dogWeight = MutableLiveData<Double>()
        val dogWeight
            get() = _dogWeight

        fun fetchDogId() {
            viewModelScope.launch {
                dogPreferences.dogId.collect { id ->
                    _dogId.value = id
                }
            }
        }

        fun fetchDogWeight() {
            viewModelScope.launch {
                dogPreferences.dogWeight.collect { weight ->
                    _dogWeight.value = weight
                }
            }
        }
    }
