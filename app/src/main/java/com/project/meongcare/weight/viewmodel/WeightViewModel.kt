package com.project.meongcare.weight.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.weight.model.data.repository.WeightRepositoryImpl
import com.project.meongcare.weight.model.entities.WeightDayResponse
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import com.project.meongcare.weight.model.entities.WeightWeeksResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel
    @Inject
    constructor(
        private val weightRepositoryImpl: WeightRepositoryImpl,
    ) : ViewModel() {
        private var _weightPosted = MutableLiveData<Int>()
        val weightPosted
            get() = _weightPosted

        private var _weightPatched = MutableLiveData<Int>()
        val weightPatched
            get() = _weightPatched

        private var _weeklyWeightGet = MutableLiveData<WeightWeeksResponse>()
        val weeklyWeightGet: LiveData<WeightWeeksResponse>
            get() = _weeklyWeightGet

        private var _monthlyWeightGet = MutableLiveData<WeightMonthResponse>()
        val monthlyWeightGet
            get() = _monthlyWeightGet

        private var _dayWeightGet = MutableLiveData<WeightDayResponse>()
        val dayWeightGet
            get() = _dayWeightGet

        fun postWeight(
            accessToken: String,
            weightPostRequest: WeightPostRequest,
        ) {
            viewModelScope.launch {
                _weightPosted.value =
                    weightRepositoryImpl.postWeight(
                        accessToken,
                        weightPostRequest,
                    )
            }
        }

        fun patchWeight(
            accessToken: String,
            weightPatchRequest: WeightPatchRequest,
        ) {
            viewModelScope.launch {
                _weightPatched.value =
                    weightRepositoryImpl.patchWeight(
                        accessToken,
                        weightPatchRequest,
                    )
            }
        }

        fun getWeeklyWeight(
            accessToken: String,
            dogId: Long,
            date: String,
        ) {
            viewModelScope.launch {
                val weightGetRequest =
                    WeightGetRequest(
                        dogId,
                        date,
                    )

                _weeklyWeightGet.value = weightRepositoryImpl.getWeeklyWeight(accessToken, weightGetRequest)
            }
        }

        fun getMonthlyWeight(
            accessToken: String,
            dogId: Long,
            date: String,
        ) {
            viewModelScope.launch {
                val weightGetRequest =
                    WeightGetRequest(
                        dogId,
                        date,
                    )

                _monthlyWeightGet.value = weightRepositoryImpl.getMonthlyWeight(accessToken, weightGetRequest)
            }
        }

        fun getDailyWeight(
            accessToken: String,
            dogId: Long,
            date: String,
        ) {
            viewModelScope.launch {
                val weightGetRequest =
                    WeightGetRequest(
                        dogId,
                        date,
                    )

                _dayWeightGet.value = weightRepositoryImpl.getDayWeight(accessToken, weightGetRequest)
            }
        }
    }
