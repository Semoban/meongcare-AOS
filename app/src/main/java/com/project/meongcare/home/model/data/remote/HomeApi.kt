package com.project.meongcare.home.model.data.remote

import com.project.meongcare.home.model.entities.HomeGetDogListResponse
import com.project.meongcare.home.model.entities.HomeGetProfileResponse
import com.project.meongcare.home.model.entities.HomeGetSymptomResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
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
    // 선택된 강아지의 이상증상 받아오는 api
    @GET("/symptom/home/{dogId}")
    suspend fun getDogSymptom(
        @Path("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
        @Header("AccessToken") accessToken: String,
    ): Response<HomeGetSymptomResponse>
}
