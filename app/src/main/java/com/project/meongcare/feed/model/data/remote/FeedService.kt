package com.project.meongcare.feed.model.data.remote

import com.project.meongcare.feed.model.entities.Feed
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedPartRecords
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.Feeds
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @Multipart
    @POST("feed")
    suspend fun postFeed(
        @Header("AccessToken") accessToken: String,
        @Part("dto") dto: RequestBody,
        @Part file: MultipartBody.Part,
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
}
