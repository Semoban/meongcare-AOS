package com.project.meongcare.onboarding.model.data.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody

interface DogAddRepository {
    suspend fun postDogInfo(accessToken: String, file: MultipartBody.Part, dto: RequestBody): Int
}
