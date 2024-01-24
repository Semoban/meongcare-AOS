package com.project.meongcare.medicalRecord.model.data.remote

import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MedicalRecordAPI {
    @Multipart
    @POST("/supplements")
    suspend fun addMedicalRecord(
        @Header("AccessToken") accessToken: String?,
        @Part filePart: MultipartBody.Part,
        @Part("dto") medicalRecordDto: RequestBody,
    ): Response<ResponseBody>
}

