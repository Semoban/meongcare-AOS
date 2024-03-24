package com.project.meongcare.aws.model.data.remote

import com.project.meongcare.aws.model.entities.AWSS3Response
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface AWSS3Api {
    @GET("aws/s3")
    suspend fun getPreSignedUrl(
        @Header("AccessToken") accessToken: String,
        @Query("fileName") fileName: String,
    ): Response<AWSS3Response>

    @Multipart
    @PUT
    suspend fun uploadImageToS3(
        @Url preSignedUrl: String,
        @Part file: MultipartBody.Part,
    ): Response<Int>
}
