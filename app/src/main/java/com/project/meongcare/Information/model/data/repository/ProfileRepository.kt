package com.project.meongcare.Information.model.data.repository

import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    suspend fun putDogInfo(
        dogId: Long,
        accessToken: String,
        file: MultipartBody.Part,
        dto: RequestBody,
    ): Int?
}
