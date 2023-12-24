package com.project.meongcare.supplement.model.data.remote

import com.project.meongcare.supplement.model.entities.ResultSupplement
import com.project.meongcare.symptom.model.entities.ResultSymptom
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SupplementAPI {
    @GET("/supplements")
    fun getResultSupplement(
        @Header("AccessToken") accessToken: String,
        @Query("dogId") dogId: Int,
        @Query("dateTime") dateTime: String,
    ): Call<ResultSupplement>
}

