package com.project.meongcare.recordShare.model.data.repository

import com.project.meongcare.recordShare.model.entities.GetDogListResponse
import retrofit2.Response

interface RecordShareRepository {
    suspend fun getDogList(accessToken: String): Response<GetDogListResponse>?
}
