package com.project.meongcare.medicalrecord.model.data.repository

import android.util.Log
import com.project.meongcare.medicalrecord.model.data.remote.MedicalRecordRetrofitClient
import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import retrofit2.Response
import javax.inject.Inject

class MedicalRecordRepositoryImpl
    @Inject
    constructor(private val medicalRecordRetrofitClient: MedicalRecordRetrofitClient) : MedicalRecordRepository {
        override suspend fun getMedicalRecordList(
            dogId: Long,
            dateTime: String,
            accessToken: String
        ): Response<MedicalRecordGetResponse>? {
            try {
                val response = medicalRecordRetrofitClient.medicalRecordApi.getMedicalRecordList(dogId, dateTime, accessToken)
                if (response.isSuccessful) {
                    Log.d("MedicalRepo-MedicalList", "통신 성공 : ${response.code()}")
                    return response
                } else {
                    Log.d("MedicalRepo-MedicalList", "통신 실패 : ${response.code()}")
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
