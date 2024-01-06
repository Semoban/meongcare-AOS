package com.project.meongcare.Information.model.data.remote

import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProfileApi {
    @GET("/member/profile")
    suspend fun getUserProfile(
        @Header("AccessToken") accessToken: String,
    ): Response<GetUserProfileResponse>

    @GET("/dog")
    suspend fun getDogList(
        @Header("AccessToken") accessToken: String,
    ): Response<GetDogListResponse>

    @GET("/dog/{dogId}")
    suspend fun getDogInfo(
        @Path("dogId") dogId: Long,
        @Header("AccessToken") accessToken: String,
    ): Response<GetDogInfoResponse>
}
