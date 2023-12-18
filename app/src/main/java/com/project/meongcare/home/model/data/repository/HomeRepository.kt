package com.project.meongcare.home.model.data.repository

import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.HomeGetExcretaResponse
import com.project.meongcare.home.model.entities.HomeGetFeedResponse
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSupplementsResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import com.project.meongcare.home.model.entities.HomeGetWeightResponse

interface HomeRepository {
    suspend fun getUserProfile(accessToken: String): HomeGetProfileResponse?

    suspend fun getDogList(accessToken: String): MutableList<DogProfile>?

    suspend fun getDogWeight(dogId: Long, date: String, accessToken: String): HomeGetWeightResponse?

    suspend fun getDogFeed(dogId: Long, date: String, accessToken: String): HomeGetFeedResponse?

    suspend fun getDogSupplements(dogId: Long, date: String, accessToken: String): HomeGetSupplementsResponse?

    suspend fun getDogExcreta(dogId: Long, dateTime: String, accessToken: String): HomeGetExcretaResponse?

    suspend fun getDogSymptom(dogId: Long, dateTime: String, accessToken: String): HomeGetSymptomResponse?
}
