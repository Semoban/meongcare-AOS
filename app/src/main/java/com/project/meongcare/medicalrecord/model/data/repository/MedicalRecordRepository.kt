package com.project.meongcare.medicalrecord.model.data.repository

import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Query

interface MedicalRecordRepository {
    suspend fun getMedicalRecordList(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): Response<MedicalRecordGetResponse>?
}
