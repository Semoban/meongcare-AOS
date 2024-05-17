package com.project.meongcare.feed.model.data.remote

import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecords
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedPostRequest
import com.project.meongcare.feed.model.entities.FeedPutRequest
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.Feeds
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

interface FeedService {
    @POST("feed")
    suspend fun postFeed(
        @Header("AccessToken") accessToken: String,
        @Body feedPostRequest: FeedPostRequest,
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

    @GET("feed/list/{dogId}")
    suspend fun getFeeds(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
    ): Response<Feeds>

    @GET("feed/list/before/{dogId}")
    suspend fun getPreviousFeed(
        @Header("AccessToken") accessToken: String,
        @Path("dogId") dogId: Long,
        @Query("feedRecordId") feedRecordId: Long,
    ): Response<FeedRecords>

    @GET("feed/detail/{feedId}")
    suspend fun getDetailFeed(
        @Header("AccessToken") accessToken: String,
        @Path("feedId") feedId: Long,
        @Query("feedRecordId") feedRecordId: Long,
    ): Response<FeedDetailGetResponse>

    @PATCH("feed")
    suspend fun patchFeed(
        @Header("AccessToken") accessToken: String,
        @Body requestBody: FeedPatchRequest,
    ): Response<Int>

    @PATCH("feed/stop/{feedRecordId}")
    suspend fun stopFeed(
        @Header("AccessToken") accessToken: String,
        @Path("feedRecordId") feedRecordId: Long,
    ): Response<Int>

    @PUT("feed")
    suspend fun putFeed(
        @Header("AccessToken") accessToken: String,
        @Body feedPutRequest: FeedPutRequest,
    ): Response<Int>

    @DELETE("feed/{feedRecordId}")
    suspend fun deleteFeed(
        @Header("AccessToken") accessToken: String,
        @Path("feedRecordId") feedRecordId: Long,
    ): Response<Int>
}
