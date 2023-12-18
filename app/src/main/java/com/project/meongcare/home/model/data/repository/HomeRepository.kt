package com.project.meongcare.home.model.data.repository

import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse

interface HomeRepository {
    suspend fun getUserProfile(accessToken: String): HomeGetProfileResponse?

    suspend fun getDogList(accessToken: String): MutableList<DogProfile>?

    suspend fun getDogSymptom(dogId: Long, dateTime: String, accessToken: String): HomeGetSymptomResponse?
}
