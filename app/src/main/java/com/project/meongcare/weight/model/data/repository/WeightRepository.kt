package com.project.meongcare.weight.model.data.repository

import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest

interface WeightRepository {
    suspend fun postWeight(weightPostRequest: WeightPostRequest): Int?

    suspend fun patchWeight(weightPatchRequest: WeightPatchRequest): Int?
}
