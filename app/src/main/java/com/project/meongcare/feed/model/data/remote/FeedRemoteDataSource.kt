package com.project.meongcare.feed.model.data.remote

import android.util.Log
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedPartRecords
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import com.project.meongcare.feed.model.entities.Feeds
import org.json.JSONObject
import javax.inject.Inject

class FeedRemoteDataSource
    @Inject
    constructor() {
        private val accessToken =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NiwiZXhwIjoxNzAzMzY1MjQ2fQ.qbSYeabyBpAni3yISWDUGYgFkQdKYfdFqPlMlz7DKCs"
        private val feedApiService = FeedClient.feedService

        suspend fun postFeed(feedUploadRequest: FeedUploadRequest): Int? {
            try {
                val postFeedResponse =
                    feedApiService.postFeed(
                        accessToken,
                        feedUploadRequest.dto,
                        feedUploadRequest.file,
                    )

                if (postFeedResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(postFeedResponse.code().toString())
                    Log.d("FeedPostFailure", postFeedResponse.code().toString())
                    Log.d("FeedPostFailure", "$stringToJson")
                    return null
                }

                Log.d("FeedPostSuccess", postFeedResponse.code().toString())
                return postFeedResponse.code()
            } catch (e: Exception) {
                Log.e("FeedPostException", e.toString())
                return null
            }
        }

        suspend fun getFeed(): FeedGetResponse? {
            try {
                val getFeedResponse =
                    feedApiService.getFeed(
                        accessToken,
                        2L,
                    )
                return if (getFeedResponse.code() == SUCCESS) {
                    Log.d("FeedGetSuccess", getFeedResponse.code().toString())
                    getFeedResponse.body()
                } else {
                    val stringToJson = JSONObject(getFeedResponse.errorBody()?.string()!!)
                    Log.d("FeedGetFailure", getFeedResponse.code().toString())
                    Log.d("FeedGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("FeedGetException", e.toString())
                return null
            }
        }

        suspend fun getFeedPart(feedRecordId: Long): FeedPartRecords? {
            try {
                val getFeedPartResponse =
                    feedApiService.getFeedPart(
                        accessToken,
                        2L,
                        feedRecordId,
                    )
                return if (getFeedPartResponse.code() == SUCCESS) {
                    Log.d("FeedPartGetSuccess", getFeedPartResponse.code().toString())
                    getFeedPartResponse.body()
                } else {
                    val stringToJson = JSONObject(getFeedPartResponse.errorBody()?.string()!!)
                    Log.d("FeedPartGetFailure", getFeedPartResponse.code().toString())
                    Log.d("FeedPartGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("FeedPartGetException", e.toString())
                return null
            }
        }

        suspend fun getFeeds(): Feeds? {
            try {
                val getFeedsResponse =
                    feedApiService.getFeeds(
                        accessToken,
                        2L,
                    )
                return if (getFeedsResponse.code() == SUCCESS) {
                    Log.d("FeedsGetSuccess", getFeedsResponse.code().toString())
                    getFeedsResponse.body()
                } else {
                    val stringToJson = JSONObject(getFeedsResponse.errorBody()?.string()!!)
                    Log.d("FeedsGetFailure", getFeedsResponse.code().toString())
                    Log.d("FeedsGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.d("FeedsGetException", e.toString())
                return null
            }
        }

        suspend fun getPreviousFeed(feedRecordId: Long): FeedRecords? {
            try {
                val getPreviousFeedResponse =
                    feedApiService.getPreviousFeed(
                        accessToken,
                        2L,
                        feedRecordId
                    )

                if (getPreviousFeedResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(getPreviousFeedResponse.errorBody()?.string()!!)
                    Log.d("PreviousFeedGetFailure", getPreviousFeedResponse.code().toString())
                    Log.d("PreviousFeedGetFailure", "$stringToJson")
                    return null
                }

                Log.d("PreviousFeedGetSuccess", getPreviousFeedResponse.code().toString())
                return getPreviousFeedResponse.body()
            } catch (e: Exception) {
                Log.d("PreviousFeedGetException", e.toString())
                return null
            }
        }

        suspend fun patchFeed(feedPatchRequest: FeedPatchRequest): Int? {
            try {
                val patchFeedResponse =
                    feedApiService.patchFeed(
                        accessToken,
                        feedPatchRequest,
                    )

                if (patchFeedResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(patchFeedResponse.errorBody()?.string()!!)
                    Log.d("FeedPatchFailure", patchFeedResponse.code().toString())
                    Log.d("FeedPatchFailure", "$stringToJson")
                    return null
                }

                Log.d("FeedPatchSuccess", patchFeedResponse.code().toString())
                return patchFeedResponse.code()
            } catch (e: Exception) {
                Log.d("FeedPatchException", e.toString())
                return null
            }
        }
    }
