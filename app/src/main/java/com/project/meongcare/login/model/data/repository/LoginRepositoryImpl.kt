package com.project.meongcare.login.model.data.repository

import android.util.Log
import com.project.meongcare.login.model.data.remote.LoginRetrofitClient
import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.model.entities.LoginResponse
import javax.inject.Inject

class LoginRepositoryImpl
    @Inject
    constructor(private val loginRetrofitClient: LoginRetrofitClient) : LoginRepository {
        override suspend fun postLoginInfo(loginRequest: LoginRequest): LoginResponse? {
            try {
                val response = loginRetrofitClient.loginApi.postLoginInfo(loginRequest)
                if (response.isSuccessful) {
                    Log.d("LoginRepository", "통신 성공 : accessToken - ${response.body()?.accessToken}")
                    return response.body()
                } else {
                    Log.d("LoginRepository", "통신 실패")
                    Log.d("LoginRepository-fail", response.code().toString())
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
