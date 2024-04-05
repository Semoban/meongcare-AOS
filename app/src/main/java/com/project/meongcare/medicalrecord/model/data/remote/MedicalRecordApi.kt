package com.project.meongcare.medicalRecord.model.data.remote

import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MedicalRecordApi {
    @GET("medical-record")
    suspend fun getMedicalRecordList(
        @Query("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
        @Header("AccessToken") accessToken: String,
    ): Response<MedicalRecordGetResponse>

    @GET("medical-record/{medicalRecordId}")
    suspend fun getMedicalRecord(
        @Path("medicalRecordId") medicalRecordId: Long,
        @Header("AccessToken") accessToken: String,
    ): Response<MedicalRecordGet>

    @DELETE("medical-record")
    suspend fun deleteMedicalRecordList(
        @Query("medicalRecordIds") medicalRecordIds: IntArray,
        @Header("AccessToken") accessToken: String,
    ): Response<Int>
}
