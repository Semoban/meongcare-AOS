package com.project.meongcare.home.model.data.repository

import android.util.Log
import com.project.meongcare.home.model.data.remote.HomeRetrofitClient
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import javax.inject.Inject

class HomeRepositoryImpl
    @Inject
    constructor(private val homeRetrofitClient: HomeRetrofitClient): HomeRepository {
        override suspend fun getUserProfile(accessToken: String): HomeGetProfileResponse? {
            try {
                val response = homeRetrofitClient.homeApi.getUserProfile(accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-UserProfile", "통신 성공 : ${response.code()}")
                    return response.body()
                } else {
                    Log.d("HomeRepo-UserProfile", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogList(accessToken: String): MutableList<DogProfile>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogList(accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogList", "통신 성공 : ${response.code()}")
                    return response.body()?.dogs
                } else {
                    Log.d("HomeRepo-DogList", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogSymptom(
            dogId: Long,
            dateTime: String,
            accessToken: String
        ): HomeGetSymptomResponse? {
            try {
                val response = homeRetrofitClient.homeApi.getDogSymptom(dogId, dateTime, accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogSymptom", "통신 성공 : ${response.code()}")
                    return response.body()
                } else {
                    Log.d("HomeRepo-DogSymptom", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
