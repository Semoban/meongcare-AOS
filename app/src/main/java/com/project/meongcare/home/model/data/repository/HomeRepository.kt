package com.project.meongcare.home.model.data.repository

import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.HomeGetExcretaResponse
import com.project.meongcare.home.model.entities.HomeGetFeedResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSupplementsResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import com.project.meongcare.home.model.entities.HomeGetWeightResponse
import com.project.meongcare.weight.model.entities.WeightPostRequest

interface HomeRepository {
    suspend fun getUserProfile(accessToken: String): GetUserProfileResponse?

    suspend fun getDogList(accessToken: String): MutableList<DogProfile>?

    suspend fun postDogWeight(
        accessToken: String,
        weightRequest: WeightPostRequest,
    ): Int?

    suspend fun getDogWeight(
        dogId: Long,
        date: String,
        accessToken: String,
    ): HomeGetWeightResponse?

    suspend fun getDogFeed(
        dogId: Long,
        date: String,
        accessToken: String,
    ): HomeGetFeedResponse?

    suspend fun getDogSupplements(
        dogId: Long,
        date: String,
        accessToken: String,
    ): HomeGetSupplementsResponse?

    suspend fun getDogExcreta(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): HomeGetExcretaResponse?

    suspend fun getDogSymptom(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): HomeGetSymptomResponse?
}
