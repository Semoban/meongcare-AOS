package com.project.meongcare.medicalRecord.model.data.remote

import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @Multipart
    @POST("medical-record")
    suspend fun addMedicalRecord(
        @Header("AccessToken") accessToken: String?,
        @Part filePart: MultipartBody.Part,
        @Part("dto") medicalRecordDto: RequestBody,
    ): Response<ResponseBody>
}
