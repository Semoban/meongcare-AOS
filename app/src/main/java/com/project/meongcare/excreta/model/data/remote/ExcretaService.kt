package com.project.meongcare.excreta.model.data.remote

import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ExcretaService {
    @GET("excreta/{dogId}")
    suspend fun getExcretaRecords(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("dateTime") dateTime: String,
    ): Response<ExcretaRecordGetResponse>
}
