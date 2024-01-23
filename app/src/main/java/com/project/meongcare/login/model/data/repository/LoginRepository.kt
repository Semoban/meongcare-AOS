package com.project.meongcare.login.model.data.repository

import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.model.entities.LoginResponse
import com.project.meongcare.login.model.entities.ReissueResponse
import retrofit2.Response

interface LoginRepository {
    suspend fun postLoginInfo(loginRequest: LoginRequest): LoginResponse?

    suspend fun getNewAccessToken(refreshToken: String): Response<ReissueResponse>?
}
