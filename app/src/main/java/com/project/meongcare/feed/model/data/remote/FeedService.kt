package com.project.meongcare.feed.model.data.remote

import com.project.meongcare.feed.model.entities.FeedGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface FeedService {
    @GET("feed/{dogId}")
    suspend fun getFeed(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
    ): Response<FeedGetResponse>
}
