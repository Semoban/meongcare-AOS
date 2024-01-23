package com.project.meongcare.onboarding.model.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DogAddApi {
    @Multipart
    @POST("/dog")
    suspend fun postDogInfo(
        @Header("AccessToken") accessToken: String,
        @Part file: MultipartBody.Part,
        @Part("dto") dto: RequestBody,
    ): Response<Int>
}
