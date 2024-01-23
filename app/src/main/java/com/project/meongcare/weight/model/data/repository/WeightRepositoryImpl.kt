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
        override suspend fun postWeight(
            accessToken: String,
            weightPostRequest: WeightPostRequest,
        ) = weightDataSource.postWeight(accessToken, weightPostRequest)

        override suspend fun patchWeight(
            accessToken: String,
            weightPatchRequest: WeightPatchRequest,
        ) = weightDataSource.patchWeight(accessToken, weightPatchRequest)

        override suspend fun getWeeklyWeight(
            accessToken: String,
            weightGetRequest: WeightGetRequest,
        ) = weightDataSource.getWeeklyWeight(accessToken, weightGetRequest)

        override suspend fun getMonthlyWeight(
            accessToken: String,
            weightGetRequest: WeightGetRequest,
        ) = weightDataSource.getMonthlyWeight(accessToken, weightGetRequest)

        override suspend fun getDayWeight(
            accessToken: String,
            weightGetRequest: WeightGetRequest,
        ) = weightDataSource.getDayWeight(accessToken, weightGetRequest)
    }
