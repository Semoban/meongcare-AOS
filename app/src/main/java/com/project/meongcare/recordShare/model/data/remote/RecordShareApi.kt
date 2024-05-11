package com.project.meongcare.recordShare.model.data.remote

import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordPutDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordShareApi {
    @GET("dog")
    suspend fun getDogList(
        @Header("AccessToken") accessToken: String,
    ): Response<GetDogListResponse>
}
