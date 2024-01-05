package com.project.meongcare.Information.model.data.remote

import com.project.meongcare.home.model.entities.GetUserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileApi {
    @GET("/member/profile")
    suspend fun getUserProfile(
        @Header("AccessToken") accessToken: String,
    ): Response<GetUserProfileResponse>
}
