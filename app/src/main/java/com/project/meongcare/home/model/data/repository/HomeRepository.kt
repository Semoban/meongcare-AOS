package com.project.meongcare.home.model.data.repository

import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.home.model.entities.HomeGetExcretaResponse
import com.project.meongcare.home.model.entities.HomeGetFeedResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSupplementsResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import com.project.meongcare.home.model.entities.HomeGetWeightResponse
import com.project.meongcare.weight.model.entities.WeightPostRequest
import retrofit2.Response

interface HomeRepository {
    suspend fun getUserProfile(accessToken: String): Response<GetUserProfileResponse>?

    suspend fun getDogList(accessToken: String): Response<GetDogListResponse>?

    suspend fun postDogWeight(
        accessToken: String,
        weightRequest: WeightPostRequest,
    ): Int?

    suspend fun getDogWeight(
        dogId: Long,
        date: String,
        accessToken: String,
    ): Response<HomeGetWeightResponse>?

    suspend fun getDogFeed(
        dogId: Long,
        date: String,
        accessToken: String,
    ): Response<HomeGetFeedResponse>?

    suspend fun getDogSupplements(
        dogId: Long,
        date: String,
        accessToken: String,
    ): Response<HomeGetSupplementsResponse>?

    suspend fun getDogExcreta(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): Response<HomeGetExcretaResponse>?

    suspend fun getDogSymptom(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): Response<HomeGetSymptomResponse>?
}
