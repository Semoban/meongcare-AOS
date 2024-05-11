package com.project.meongcare.recordShare.model.data.repository

import android.util.Log
import com.project.meongcare.recordShare.model.entities.GetDogListResponse
import com.project.meongcare.recordShare.model.data.remote.RecordShareRetrofitClient
import retrofit2.Response
import javax.inject.Inject

class RecordShareShareRepositoryImpl
    @Inject
    constructor(private val recordShareRetrofitClient: RecordShareRetrofitClient) : RecordShareRepository {
        override suspend fun getDogList(accessToken: String): Response<GetDogListResponse>? {
            try {
                val response = recordShareRetrofitClient.recordShareApi.getDogList(accessToken)
                return if (response.isSuccessful) {
                    Log.d("RecordShareRepo-DogList", "통신 성공 : ${response.code()}")
                    response
                } else {
                    Log.d("RecordShareRepo-DogList", "통신 실패 : ${response.code()}")
                    response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

}
