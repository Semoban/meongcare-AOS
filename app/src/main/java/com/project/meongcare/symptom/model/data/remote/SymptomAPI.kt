package com.project.meongcare.symptom.model.data.remote

import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SymptomAPI {
    @GET("/symptom/{dogId}")
    suspend fun getSymptomList(
        @Header("AccessToken") accessToken: String?,
        @Path("dogId") dogId: Long?,
        @Query("dateTime") dateTime: String,
    ): Response<ResultSymptom>

    @POST("/symptom")
    suspend fun addSymptom(
        @Header("AccessToken") accessToken: String?,
        @Body requestBody: ToAddSymptom,
    ): Response<ResponseBody>

    @DELETE("/symptom")
    suspend fun deleteSymptom(
        @Header("AccessToken") accessToken: String,
        @Query("symptomIds") symtomIds: IntArray,
    ): Response<ResponseBody>

    @PATCH("/symptom")
    suspend fun patchSymptom(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: ToEditSymptom,
    ): Response<ResponseBody>
}

