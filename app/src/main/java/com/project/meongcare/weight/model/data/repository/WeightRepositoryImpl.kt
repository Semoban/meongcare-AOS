package com.project.meongcare.weight.model.data.repository

import com.project.meongcare.weight.model.data.remote.WeightRemoteDataSource
import com.project.meongcare.weight.model.entities.WeightPostRequest
import javax.inject.Inject

class WeightRepositoryImpl @Inject constructor(
    private val weightDataSource: WeightRemoteDataSource
) : WeightRepository {
    override suspend fun postWeight(weightPostRequest: WeightPostRequest) =
        weightDataSource.postWeight(weightPostRequest)
}
