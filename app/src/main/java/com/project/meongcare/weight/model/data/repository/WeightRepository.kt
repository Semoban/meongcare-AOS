package com.project.meongcare.weight.model.data.repository

import com.project.meongcare.weight.model.entities.WeightDayResponse
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import com.project.meongcare.weight.model.entities.WeightWeekResponse

interface WeightRepository {
    suspend fun postWeight(weightPostRequest: WeightPostRequest): Int?

    suspend fun patchWeight(weightPatchRequest: WeightPatchRequest): Int?

    suspend fun getWeeklyWeight(weightGetRequest: WeightGetRequest): WeightWeekResponse?

    suspend fun getMonthlyWeight(weightGetRequest: WeightGetRequest): WeightMonthResponse?

    suspend fun getDayWeight(weightGetRequest: WeightGetRequest): WeightDayResponse?
}
