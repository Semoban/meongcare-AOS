package com.project.meongcare.medicalRecord.model.data.repository

import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordPutDto
import retrofit2.Response

interface MedicalRecordRepository {
    suspend fun getMedicalRecordList(
        dogId: Long,
        dateTime: String,
        accessToken: String,
    ): Response<MedicalRecordGetResponse>?

    suspend fun getMedicalRecord(
        medicalRecordId: Long,
        accessToken: String,
    ): Response<MedicalRecordGet>?

    suspend fun deleteMedicalRecordList(
        medicalRecordIds: IntArray,
        accessToken: String,
    ): Int?

    suspend fun addMedicalRecord(
        accessToken: String?,
        medicalRecordDto: MedicalRecordDto,
    ): Int

    suspend fun putMedicalRecord(
        accessToken: String?,
        medicalRecordPutDto: MedicalRecordPutDto,
    ): Int
}
