package com.project.meongcare.info.model.data.repository

import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import com.project.meongcare.info.model.entities.DogPutRequest
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.info.model.entities.ProfilePatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import retrofit2.Response

interface ProfileRepository {
    suspend fun getUserProfile(accessToken: String): Response<GetUserProfileResponse>?

    suspend fun getDogList(accessToken: String): Response<GetDogListResponse>?

    suspend fun getdogInfo(
        dogId: Long,
        accessToken: String,
    ): Response<GetDogInfoResponse>?

    suspend fun deleteDog(
        dogId: Long,
        accessToken: String,
    ): Int?

    suspend fun putDogInfo(
        dogId: Long,
        accessToken: String,
        dogPutRequest: DogPutRequest,
    ): Int?

    suspend fun postDogWeight(
        accessToken: String,
        requestBody: WeightPostRequest,
    ): Int?

    suspend fun patchDogWeight(
        dogId: Long,
        kg: Double,
        date: String,
        accessToken: String,
    ): Int?

    suspend fun logoutUser(refreshToken: String): Int?

    suspend fun deleteUser(accessToken: String): Int?

    suspend fun patchPushAgreement(
        pushAgreement: Boolean,
        accessToken: String,
    ): Int?

    suspend fun patchProfileImage(
        accessToken: String,
        profilePatchRequest: ProfilePatchRequest,
    ): Int?
}
