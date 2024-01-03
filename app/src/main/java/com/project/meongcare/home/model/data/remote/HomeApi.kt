package com.project.meongcare.home.model.data.remote

import com.project.meongcare.home.model.entities.HomeGetDogListResponse
import com.project.meongcare.home.model.entities.HomeGetExcretaResponse
import com.project.meongcare.home.model.entities.HomeGetFeedResponse
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSupplementsResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import com.project.meongcare.home.model.entities.HomeGetWeightResponse
import com.project.meongcare.weight.model.entities.WeightPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    // 유저 프로필 받아오는 api
    @GET("/member/profile")
    suspend fun getUserProfile(
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetProfileResponse>

    // 강아지 목록 받아오는 api
    @GET("/dog")
    suspend fun getDogList(
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetDogListResponse>

    // 체중 Post api
    @POST("/weight")
    suspend fun postDogWeight(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: WeightPostRequest,
    ): Response<Int>

    // 선택된 강아지의 체중 받아오는 api
    @GET("/weight/home/{dogId}")
    suspend fun getDogWeight(
        @Path("dogId") dogId: Long,
        @Query("date") date: String,
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetWeightResponse>

    // 선택된 강아지의 사료 섭취량 받아오는 api
    @GET("/feed/home/{dogId}")
    suspend fun getDogFeed(
        @Path("dogId") dogId: Long,
        @Query("date") date: String,
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetFeedResponse>

    // 선택된 강아지의 영양체 섭취율 받아오는 api
    @GET("/supplements/home/{dogId}")
    suspend fun getDogSupplements(
        @Path("dogId") dogId: Long,
        @Query("date") date: String,
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetSupplementsResponse>

    // 선택된 강아지의 대소변 횟수 받아오는 api
    @GET("/excreta/home/{dogId}")
    suspend fun getDogExcreta(
        @Path("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetExcretaResponse>

    // 선택된 강아지의 이상증상 받아오는 api
    @GET("/symptom/home/{dogId}")
    suspend fun getDogSymptom(
        @Path("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetSymptomResponse>
}
