package com.project.meongcare.supplement.model.data.remote

import com.project.meongcare.supplement.model.entities.InfoSupplement
import com.project.meongcare.supplement.model.entities.ResultSupplement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
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

interface SupplementAPI {
    @GET("/supplements")
    suspend fun getResultSupplement(
        @Header("AccessToken") accessToken: String,
        @Query("date") date: String,
        @Query("dogId") dogId: Int,
    ): Response<ResultSupplement>

    @GET("/supplements/dog/{dogId}")
    suspend fun getSupplementInfos(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Int,
    ): Response<InfoSupplement>

    @Multipart
    @POST("/supplements")
    fun addSupplement(
        @Header("AccessToken") accessToken: String,
        @Part filePart: MultipartBody.Part,
        @Part("dto") supplementDto: RequestBody,
    ): Call<ResponseBody>

    @PATCH("/supplements/check")
    suspend fun checkSupplement(
        @Header("AccessToken") accessToken: String,
        @Query("supplementsRecordId") supplementsRecordId: Int,
    ): Response<ResponseBody>

    @PATCH("/supplements/alarm")
    suspend fun patchSupplementAlarmStatus(
        @Header("AccessToken") accessToken: String,
        @Query("supplementsId") supplementsId: Int,
        @Query("pushAgreement") pushAgreement: Boolean,
    ): Response<ResponseBody>

    @DELETE("/supplements")
    suspend fun deleteSupplementsById(
        @Header("AccessToken") accessToken: String,
        @Query("supplementsIds") supplementsRecordId: IntArray,
    ): Response<ResponseBody>
}

