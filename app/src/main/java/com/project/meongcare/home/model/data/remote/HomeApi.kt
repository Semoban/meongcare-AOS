package com.project.meongcare.home.model.data.remote

import com.project.meongcare.home.model.entities.HomeProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeApi {
    @GET("/member/profile")
    suspend fun getUserProfile(
        @Header("AccessToken") accessToken: String,
    ): Response<HomeProfileResponse>
}
