package com.project.meongcare.weight.model.data.repository

import com.project.meongcare.weight.model.data.remote.WeightRemoteDataSource
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import javax.inject.Inject

class WeightRepositoryImpl
    @Inject
    constructor(
        private val weightDataSource: WeightRemoteDataSource,
    ) : WeightRepository {
        override suspend fun postWeight(weightPostRequest: WeightPostRequest)
            = weightDataSource.postWeight(weightPostRequest)

        override suspend fun patchWeight(weightPatchRequest: WeightPatchRequest)
            = weightDataSource.patchWeight(weightPatchRequest)

        override suspend fun getWeeklyWeight(weightGetRequest: WeightGetRequest)
            = weightDataSource.getWeeklyWeight(weightGetRequest)

        override suspend fun getMonthlyWeight(weightGetRequest: WeightGetRequest)
            = weightDataSource.getMonthlyWeight(weightGetRequest)

        override suspend fun getDayWeight(weightGetRequest: WeightGetRequest)
            = weightDataSource.getDayWeight(weightGetRequest)
    }
