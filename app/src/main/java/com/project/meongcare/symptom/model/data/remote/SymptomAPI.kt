package com.project.meongcare.symptom.model.data.remote

import com.project.meongcare.symptom.model.entities.ResponseSymptom
import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import retrofit2.Call
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
    fun getResultSymptom(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Int,
        @Query("dateTime") dateTime: String,
    ): Call<ResultSymptom>

    @POST("/symptom")
    fun addSymptom(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: ToAddSymptom,
    ): Call<ResponseSymptom>

    @DELETE("/symptom")
    fun deleteSymptom(
        @Header("AccessToken") accessToken: String,
        @Query("symptomIds") symtomIds: IntArray,
    ): Call<ResponseSymptom>

    @PATCH("/symptom")
    fun editSymptom(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: ToEditSymptom,
    ): Call<ResponseSymptom>
}

