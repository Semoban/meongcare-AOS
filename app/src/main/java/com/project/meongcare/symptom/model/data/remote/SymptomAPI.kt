package com.project.meongcare.symptom.model.data.remote

import com.project.meongcare.symptom.model.entities.ResponseSymptom
import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface SymptomAPI {
    @GET("/symptom/{dogId}")
    fun getResultSymptom(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Int,
        @Query("dateTime") dateTime: LocalDateTime,
    ): Call<ResultSymptom>

    @POST("/symptom")
    fun addSymptom(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: ToAddSymptom,
    ): Call<ResponseSymptom>
}

