package com.project.meongcare.login.model.data.repository

import android.util.Log
import com.project.meongcare.login.model.data.remote.LoginRetrofitClient
import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.model.entities.LoginResponse
import com.project.meongcare.login.model.entities.ReissueResponse
import javax.inject.Inject

class LoginRepositoryImpl
    @Inject
    constructor(private val loginRetrofitClient: LoginRetrofitClient) : LoginRepository {
        override suspend fun postLoginInfo(loginRequest: LoginRequest): LoginResponse? {
            try {
                val response = loginRetrofitClient.loginApi.postLoginInfo(loginRequest)
                if (response.isSuccessful) {
                    Log.d("LoginRepository", "통신 성공 : ${response.code()}")
                    return response.body()
                } else {
                    Log.d("LoginRepository", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getNewAccessToken(refreshToken: String): ReissueResponse? {
            try {
                val response = loginRetrofitClient.loginApi.getNewAccessToken(refreshToken)
                if (response.isSuccessful) {
                    Log.d("LoginRepository", "통신 성공 : ${response.code()}")
                    return response.body()
                } else {
                    Log.d("LoginRepository", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
