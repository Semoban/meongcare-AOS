package com.project.meongcare.login.model.data.repository

import android.util.Log
import com.project.meongcare.RetrofitClient
import com.project.meongcare.login.model.data.remote.LoginApi
import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.model.entities.LoginResponse
import com.project.meongcare.login.model.entities.ReissueResponse
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl
    @Inject
    constructor(retrofitClient: RetrofitClient) : LoginRepository {
        private val loginApi = retrofitClient.createApi<LoginApi>()
        override suspend fun postLoginInfo(loginRequest: LoginRequest): Response<LoginResponse>? {
            return try {
                val response = loginApi.postLoginInfo(loginRequest)
                if (response.isSuccessful) {
                    Log.d("LoginRepository", "통신 성공 : ${response.code()}")
                    response
                } else {
                    Log.d("LoginRepository", "통신 실패 : ${response.code()}")
                    response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun getNewAccessToken(refreshToken: String): Response<ReissueResponse>? {
            return try {
                val response = loginApi.getNewAccessToken(refreshToken)
                if (response.isSuccessful) {
                    Log.d("LoginRepository", "통신 성공 : ${response.code()}")
                    response
                } else {
                    Log.d("LoginRepository", "통신 실패 : ${response.code()}")
                    response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
