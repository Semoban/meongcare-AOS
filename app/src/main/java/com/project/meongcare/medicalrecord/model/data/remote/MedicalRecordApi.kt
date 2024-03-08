package com.project.meongcare.medicalrecord.model.data.remote

import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MedicalRecordApi {
    @GET("medical-record")
    suspend fun getMedicalRecordList(
        @Query("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
        @Header("AccessToken") accessToken: String,
    ): Response<MedicalRecordGetResponse>
}
