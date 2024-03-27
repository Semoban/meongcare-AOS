package com.project.meongcare.medicalrecord.model.data.repository

import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import retrofit2.Response

interface MedicalRecordRepository {
    suspend fun getMedicalRecordList(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): Response<MedicalRecordGetResponse>?

    suspend fun deleteMedicalRecordList(
        medicalRecordIds: IntArray,
        accessToken: String,
    ): Int?
}
