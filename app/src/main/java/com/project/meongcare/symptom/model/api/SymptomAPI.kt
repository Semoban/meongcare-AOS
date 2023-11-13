package com.project.meongcare.symptom.model.api

import com.project.meongcare.symptom.model.ResultSymptom
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SymptomAPI {
    @GET("/symptom")
    fun getResultSymptom(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("dateTime") dateTime: String
    ): Call<ResultSymptom>

}