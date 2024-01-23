package com.project.meongcare.login.model.data.remote

import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.model.entities.LoginResponse
import com.project.meongcare.login.model.entities.ReissueResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginApi {
    @POST("/auth/login")
    suspend fun postLoginInfo(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>

    @GET("/auth/reissue")
    suspend fun getNewAccessToken(
        @Header("RefreshToken") refreshToken: String,
    ): Response<ReissueResponse>
}
