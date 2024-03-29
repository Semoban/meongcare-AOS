package com.project.meongcare.home.model.data.repository

import android.util.Log
import com.project.meongcare.home.model.data.remote.HomeRetrofitClient
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.home.model.entities.HomeGetExcretaResponse
import com.project.meongcare.home.model.entities.HomeGetFeedResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSupplementsResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import com.project.meongcare.home.model.entities.HomeGetWeightResponse
import com.project.meongcare.weight.model.entities.WeightPostRequest
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class HomeRepositoryImpl
    @Inject
    constructor(private val homeRetrofitClient: HomeRetrofitClient) : HomeRepository {
        override suspend fun getUserProfile(accessToken: String): Response<GetUserProfileResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getUserProfile(accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-UserProfile", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-UserProfile", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogList(accessToken: String): Response<GetDogListResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogList(accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogList", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-DogList", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun postDogWeight(
            accessToken: String,
            weightRequest: WeightPostRequest,
        ): Int? {
            try {
                val response = homeRetrofitClient.homeApi.postDogWeight(accessToken, weightRequest)
                if (response.code() == 200) {
                    Log.d("HomeRepo-PostDogWeight", "통신 성공 : ${response.code()}")
                    return response.code()
                } else {
                    Log.d("HomeRepo-PostDogWeight", "통신 실패 : ${response.code()}")
                    val stringToJson = JSONObject(response.errorBody()?.string()!!)
                    Log.d("HomeRepo-PostDogWeight", "$stringToJson")
                    return response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogWeight(
            dogId: Long,
            date: String,
            accessToken: String,
        ): Response<HomeGetWeightResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogWeight(dogId, date, accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-GetDogWeight", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-GetDogWeight", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogFeed(
            dogId: Long,
            date: String,
            accessToken: String,
        ): Response<HomeGetFeedResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogFeed(dogId, date, accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogFeed", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-DogFeed", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogSupplements(
            dogId: Long,
            date: String,
            accessToken: String,
        ): Response<HomeGetSupplementsResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogSupplements(dogId, date, accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogSupplements", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-DogSupplements", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogExcreta(
            dogId: Long,
            dateTime: String,
            accessToken: String,
        ): Response<HomeGetExcretaResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogExcreta(dogId, dateTime, accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogExcreta", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-DogExcreta", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogSymptom(
            dogId: Long,
            dateTime: String,
            accessToken: String,
        ): Response<HomeGetSymptomResponse>? {
            try {
                val response = homeRetrofitClient.homeApi.getDogSymptom(dogId, dateTime, accessToken)
                if (response.isSuccessful) {
                    Log.d("HomeRepo-DogSymptom", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("HomeRepo-DogSymptom", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
