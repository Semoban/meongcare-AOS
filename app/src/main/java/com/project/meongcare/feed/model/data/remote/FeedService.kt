package com.project.meongcare.feed.model.data.remote

import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecords
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @POST("feed")
    suspend fun postFeed(
        @Header("AccessToken") accessToken: String,
    ): Response<Int>

    @GET("feed/{dogId}")
    suspend fun getFeed(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
    ): Response<FeedGetResponse>

    @GET("feed/part/{dogId}")
    suspend fun getFeedPart(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("feedRecordId") feedRecordId: Long,
    ): Response<FeedPartRecords>
}
