package com.project.meongcare.info.model.data.remote

import com.project.meongcare.home.model.entities.GetDogListResponse
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.info.model.entities.DogPutRequest
import com.project.meongcare.info.model.entities.ProfilePatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {
    @GET("member/profile")
    suspend fun getUserProfile(
        @Header("AccessToken") accessToken: String,
    ): Response<GetUserProfileResponse>

    @GET("dog")
    suspend fun getDogList(
        @Header("AccessToken") accessToken: String,
    ): Response<GetDogListResponse>

    @GET("dog/{dogId}")
    suspend fun getDogInfo(
        @Path("dogId") dogId: Long,
        @Header("AccessToken") accessToken: String,
    ): Response<GetDogInfoResponse>

    @DELETE("dog/{dogId}")
    suspend fun deleteDog(
        @Path("dogId") dogId: Long,
        @Header("AccessToken") accessToken: String,
    ): Response<Int>

    @PUT("dog/{dogId}")
    suspend fun putDogInfo(
        @Path("dogId") dogId: Long,
        @Header("AccessToken") accessToken: String,
        @Body dogPutRequest: DogPutRequest,
    ): Response<Int>

    @POST("weight")
    suspend fun postDogWeight(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: WeightPostRequest,
    ): Response<Int>

    @PATCH("weight/{dogId}")
    suspend fun patchDogWeight(
        @Path("dogId") dogId: Long,
        @Query("kg") kg: Double,
        @Query("date") date: String,
        @Header("AccessToken") accessToken: String,
    ): Response<Int>

    @DELETE("auth/logout")
    suspend fun logoutUser(
        @Header("RefreshToken") refreshToken: String,
    ): Response<Int>

    @DELETE("member")
    suspend fun deleteUser(
        @Header("AccessToken") accessToken: String,
    ): Response<Int>

    @PATCH("member/alarm")
    suspend fun patchPushAgreement(
        @Query("pushAgreement") pushAgreement: Boolean,
        @Header("AccessToken") accessToken: String,
    ): Response<Int>

    @PATCH("member/profile")
    suspend fun patchProfileImage(
        @Header("AccessToken") accessToken: String,
        @Body profilePatchRequest: ProfilePatchRequest,
    ): Response<Int>
}
