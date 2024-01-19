package com.project.meongcare.weight.model.data.repository

import com.project.meongcare.weight.model.entities.WeightDayResponse
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import com.project.meongcare.weight.model.entities.WeightWeeksResponse

interface WeightRepository {
    suspend fun postWeight(
        accessToken: String,
        weightPostRequest: WeightPostRequest,
    ): Int?

    suspend fun patchWeight(
        accessToken: String,
        weightPatchRequest: WeightPatchRequest,
    ): Int?

    suspend fun getWeeklyWeight(
        accessToken: String,
        weightGetRequest: WeightGetRequest,
    ): WeightWeeksResponse?

    suspend fun getMonthlyWeight(
        accessToken: String,
        weightGetRequest: WeightGetRequest,
    ): WeightMonthResponse?

    suspend fun getDayWeight(
        accessToken: String,
        weightGetRequest: WeightGetRequest,
    ): WeightDayResponse?
}
