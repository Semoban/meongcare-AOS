package com.project.meongcare.excreta.model.data.remote

import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ExcretaService {
    @Multipart
    @POST("excreta")
    suspend fun postExcreta(
        @Header("AccessToken") accessToken: String,
        @Part("dto") dto: RequestBody,
        @Part file: MultipartBody.Part,
    ): Response<Int>

    @GET("excreta/{dogId}")
    suspend fun getExcretaRecords(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
    ): Response<ExcretaRecordGetResponse>

    @GET("excreta/detail/{excretaId}")
    suspend fun getExcretaDetail(
        @Header("AccessToken") accessToken: String,
        @Path("excretaId") excretaId: Long,
    ): Response<ExcretaDetailGetResponse>

    @DELETE("excreta")
    suspend fun deleteExcreta(
        @Header("AccessToken") accessToken: String,
        @Query("excretaIds") excretaIds: IntArray,
    ): Response<Int>

    @PATCH("excreta")
    suspend fun patchExcreta(
        @Header("AccessToken") accessToken: String,
        @Part("dto") dto: RequestBody,
        @Part file: MultipartBody.Part,
    ): Response<Int>
}
