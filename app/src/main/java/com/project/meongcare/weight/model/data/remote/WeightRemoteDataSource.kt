package com.project.meongcare.weight.model.data.remote

import android.util.Log
import com.project.meongcare.weight.model.entities.WeightDayResponse
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import com.project.meongcare.weight.model.entities.WeightWeeksResponse
import org.json.JSONObject
import javax.inject.Inject

class WeightRemoteDataSource
    @Inject
    constructor() {
        private val weightApiService = WeightClient.weightService

        suspend fun postWeight(
            accessToken: String,
            weightPostRequest: WeightPostRequest,
        ): Int? {
            try {
                val postResponse =
                    weightApiService.postWeight(
                        accessToken,
                        weightPostRequest,
                    )

                if (postResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(postResponse.errorBody()?.string()!!)
                    Log.d("WeightFailure", postResponse.code().toString())
                    Log.d("WeightFailure", "$stringToJson")
                    return null
                }

                Log.d("WeightSuccess", postResponse.code().toString())
                return postResponse.code()
            } catch (e: Exception) {
                Log.e("WeightException", e.toString())
                return null
            }
        }

        suspend fun patchWeight(
            accessToken: String,
            weightPatchRequest: WeightPatchRequest,
        ): Int? {
            try {
                val patchResponse =
                    weightApiService.patchWeight(
                        accessToken,
                        weightPatchRequest.dogId,
                        weightPatchRequest.kg,
                        weightPatchRequest.date,
                    )

                if (patchResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(patchResponse.errorBody()?.string()!!)
                    Log.d("WeightPatchFailure", patchResponse.code().toString())
                    Log.d("WeightPatchFailure", "$stringToJson")
                    return null
                }

                Log.d("WeightPatchSuccess", patchResponse.code().toString())
                return patchResponse.code()
            } catch (e: Exception) {
                Log.e("WeightPatchException", e.toString())
                return null
            }
        }

        suspend fun getWeeklyWeight(
            accessToken: String,
            weightGetRequest: WeightGetRequest,
        ): WeightWeeksResponse? {
            try {
                val getWeeklyResponse =
                    weightApiService.getWeeklyWeight(
                        accessToken,
                        weightGetRequest.dogId,
                        weightGetRequest.date,
                    )

                return if (getWeeklyResponse.code() == SUCCESS) {
                    Log.d("WeeklyWeightGetSuccess", getWeeklyResponse.code().toString())
                    getWeeklyResponse.body()
                } else {
                    val stringToJson = JSONObject(getWeeklyResponse.errorBody()?.string()!!)
                    Log.d("WeeklyWeightGetFailure", getWeeklyResponse.code().toString())
                    Log.d("WeeklyWeightGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("WeeklyWeightGetException", e.toString())
                return null
            }
        }

        suspend fun getMonthlyWeight(
            accessToken: String,
            weightGetRequest: WeightGetRequest,
        ): WeightMonthResponse? {
            try {
                val getMonthlyResponse =
                    weightApiService.getMonthlyWeight(
                        accessToken,
                        weightGetRequest.dogId,
                        weightGetRequest.date,
                    )

                return if (getMonthlyResponse.code() == SUCCESS) {
                    Log.d("MonthlyWeightGetSuccess", getMonthlyResponse.code().toString())
                    getMonthlyResponse.body()
                } else {
                    val stringToJson = JSONObject(getMonthlyResponse.errorBody()?.string()!!)
                    Log.d("MonthlyWeightGetFailure", getMonthlyResponse.code().toString())
                    Log.d("MonthlyWeightGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("MonthlyWeightGetException", e.toString())
                return null
            }
        }

        suspend fun getDayWeight(
            accessToken: String,
            weightGetRequest: WeightGetRequest,
        ): WeightDayResponse? {
            try {
                val getDayResponse =
                    weightApiService.getDayWeight(
                        accessToken,
                        weightGetRequest.dogId,
                        weightGetRequest.date,
                    )

                return if (getDayResponse.code() == SUCCESS) {
                    Log.d("DailyWeightGetSuccess", getDayResponse.code().toString())
                    getDayResponse.body()
                } else {
                    val stringToJson = JSONObject(getDayResponse.errorBody()?.string()!!)
                    Log.d("DailyWeightGetFailure", getDayResponse.code().toString())
                    Log.d("DailyWeightGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("DailyWeightGetException", e.toString())
                return null
            }
        }

        companion object {
            const val SUCCESS = 200
        }
    }
