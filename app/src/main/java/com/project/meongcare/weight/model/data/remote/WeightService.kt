package com.project.meongcare.weight.model.data.remote

import com.project.meongcare.weight.model.entities.WeightDayResponse
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightPostRequest
import com.project.meongcare.weight.model.entities.WeightWeekResponse
import com.project.meongcare.weight.model.entities.WeightWeeksResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WeightService {
    @POST("weight")
    suspend fun postWeight(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: WeightPostRequest,
    ): Response<Int>

    @PATCH("weight/{dogId}")
    suspend fun patchWeight(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("kg") kg: Double,
        @Query("date") date: String,
    ): Response<Int>

    @GET("weight/week/{dogId}")
    suspend fun getWeeklyWeight(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("date") date: String,
    ): Response<WeightWeeksResponse>

    @GET("weight/month/{dogId}")
    suspend fun getMonthlyWeight(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("date") date: String,
    ): Response<WeightMonthResponse>

    @GET("weight/day/{dogId}")
    suspend fun getDayWeight(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("date") date: String,
    ): Response<WeightDayResponse>

}
