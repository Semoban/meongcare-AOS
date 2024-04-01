package com.project.meongcare.onboarding.model.data.repository

import android.util.Log
import com.project.meongcare.onboarding.model.data.remote.DogAddRetrofitClient
import com.project.meongcare.onboarding.model.entities.DogPostRequest
import javax.inject.Inject

class DogAddRepositoryImpl
    @Inject
    constructor(private val dogAddRetrofitClient: DogAddRetrofitClient) : DogAddRepository {
        override suspend fun postDogInfo(
            accessToken: String,
            dogPostRequest: DogPostRequest,
        ): Int {
            try {
                val response = dogAddRetrofitClient.dogAddApi.postDogInfo(accessToken, dogPostRequest)
                if (response.isSuccessful) {
                    Log.d("DogAddRepository", "통신 성공(code-${response.code()})")
                    return response.code()
                } else {
                    Log.d("DogAddRepository", "통신 실패(code-${response.code()})")
                    return response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return -1
            }
        }
    }
