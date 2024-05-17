package com.project.meongcare.onboarding.model.data.remote

import com.project.meongcare.onboarding.model.entities.DogPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DogAddApi {
    @POST("dog")
    suspend fun postDogInfo(
        @Header("AccessToken") accessToken: String,
        @Body dogPostRequest: DogPostRequest,
    ): Response<Int>
}
