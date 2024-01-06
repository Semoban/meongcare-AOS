package com.project.meongcare.supplement.model.data.remote

import com.project.meongcare.supplement.model.entities.ResultSupplement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SupplementAPI {
    @GET("/supplements")
    fun getResultSupplement(
        @Header("AccessToken") accessToken: String,
        @Query("date") date: String,
        @Query("dogId") dogId: Int,
    ): Call<ResultSupplement>

    @Multipart
    @POST("/supplements")
    fun addSupplement(
        @Header("AccessToken") accessToken: String,
        @Part filePart: MultipartBody.Part,
        @Part("dto") supplementDto: RequestBody,
    ): Call<ResponseBody>

}

