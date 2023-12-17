package com.project.meongcare.weight.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.weight.model.data.repository.WeightRepositoryImpl
import com.project.meongcare.weight.model.entities.WeightDayResponse
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightPatchRequest
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

    private var weightPatched = MutableLiveData<Boolean>()
    private val _weightPatched
        get() = weightPatched

    private var weeklyWeightGet = MutableLiveData<Boolean>()
    private val _weeklyWeightGet
        get() = weeklyWeightGet

    private var _monthlyWeightGet = MutableLiveData<WeightMonthResponse>()
    val monthlyWeightGet
        get() = _monthlyWeightGet

    private var _dayWeightGet = MutableLiveData<WeightDayResponse>()
    val dayWeightGet
        get() = _dayWeightGet

    fun postWeight(
        dateTime: String,
        weight: Double? = null,
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

    fun patchWeight(
        kg: Double,
        date: String,
    ) {
        viewModelScope.launch {
            val weightPatchRequest = WeightPatchRequest(
                2L,
                kg,
                date,
            )

            weightRepositoryImpl.patchWeight(weightPatchRequest)
            _weightPatched.value = true

            Log.d("hye", weightPatchRequest.toString())
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

    fun getMonthlyWeight(
        date: String,
    ) {
        viewModelScope.launch {
            val weightGetRequest = WeightGetRequest(
                2L,
                date,
            )

            _monthlyWeightGet.value = weightRepositoryImpl.getMonthlyWeight(weightGetRequest)
        }
    }

    fun getDailyWeight(
        date: String,
    ) {
        viewModelScope.launch {
            val weightGetRequest = WeightGetRequest(
                2L,
                date,
            )

            _dayWeightGet.value = weightRepositoryImpl.getDayWeight(weightGetRequest)
        }
    }

}
