package com.project.meongcare.weight.model.data.remote

import com.project.meongcare.weight.model.entities.WeightPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface WeightService {
    @POST("weight")
    suspend fun postWeight(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: WeightPostRequest,
    ): Response<Int>
}
