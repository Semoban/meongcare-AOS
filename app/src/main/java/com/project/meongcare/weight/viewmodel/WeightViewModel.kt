package com.project.meongcare.weight.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.weight.model.data.repository.WeightRepositoryImpl
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val weightRepositoryImpl: WeightRepositoryImpl,
): ViewModel() {
    private var weightPosted = MutableLiveData<Boolean>()
    private val _weightPosted
        get() = weightPosted

    private var weeklyWeightGet = MutableLiveData<Boolean>()
    private val _weeklyWeightGet
        get() = weeklyWeightGet

    fun postWeight(
        dateTime: String,
        weight: Double,
    ) {
        viewModelScope.launch {
            val weightPostRequest = WeightPostRequest(
                2L,
                dateTime,
                weight,
            )

            weightRepositoryImpl.postWeight(weightPostRequest)
            _weightPosted.value = true

            Log.d("hye", weightPostRequest.toString())
        }
    }

    fun getWeeklyWeight(
        date: String,
    ) {
        viewModelScope.launch {
            val weightGetRequest = WeightGetRequest(
                2L,
                date,
            )

            weightRepositoryImpl.getWeeklyWeight(weightGetRequest)
            _weeklyWeightGet.value = true

            Log.d("hye", weightGetRequest.toString())
        }
    }
}
