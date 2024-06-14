package com.project.meongcare.recordShare.model.data.remote

import com.project.meongcare.recordShare.model.entities.GetDogListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface RecordShareApi {
    @GET("dog")
    suspend fun getDogList(
        @Header("AccessToken") accessToken: String,
    ): Response<GetDogListResponse>

    @GET("member/exist/{email}")
    suspend fun getIsValidEmail(
        @Path("email") email: String,
        @Header("AccessToken") accessToken: String,
    ): Response<Int>
}
