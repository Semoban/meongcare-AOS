package com.project.meongcare.weight.model.data.remote

import com.project.meongcare.weight.model.entities.WeightPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface WeightService {
    @POST("weight")
    suspend fun postWeight(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: WeightPostRequest,
    ): Response<Int>

    @PATCH("weight/{dogId}")
    suspend fun patchWeight(
        @Header("AccessToken") accessToken: String,
        @Query("kg") kg: Double,
        @Query("date") date: String,
    ): Response<Int>
}
