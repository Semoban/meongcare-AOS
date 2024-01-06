package com.project.meongcare.Information.model.data.repository

import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetUserProfileResponse

interface ProfileRepository {
    suspend fun getUserProfile(accessToken: String): GetUserProfileResponse?

    suspend fun getDogList(accessToken: String): MutableList<DogProfile>?

    suspend fun getdogInfo(
        dogId: Long,
        accessToken: String,
    ): GetDogInfoResponse?

    suspend fun deleteDog(
        dogId: Long,
        accessToken: String,
    ): Int?
}
