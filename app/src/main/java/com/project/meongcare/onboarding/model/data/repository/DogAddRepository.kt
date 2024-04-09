package com.project.meongcare.onboarding.model.data.repository

import com.project.meongcare.onboarding.model.entities.DogPostRequest

interface DogAddRepository {
    suspend fun postDogInfo(
        accessToken: String,
        dogPostRequest: DogPostRequest,
    ): Int
}
