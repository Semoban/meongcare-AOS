package com.project.meongcare.onboarding.model.data.repository

import android.util.Log
import com.project.meongcare.RetrofitClient
import com.project.meongcare.onboarding.model.data.remote.DogAddApi
import com.project.meongcare.onboarding.model.entities.DogPostRequest
import javax.inject.Inject

class DogAddRepositoryImpl
    @Inject
    constructor(retrofitClient: RetrofitClient) : DogAddRepository {
        private val dogAddApi = retrofitClient.createApi<DogAddApi>()
        override suspend fun postDogInfo(
            accessToken: String,
            dogPostRequest: DogPostRequest,
        ): Int {
            try {
                val response = dogAddApi.postDogInfo(accessToken, dogPostRequest)
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
