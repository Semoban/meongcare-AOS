package com.project.meongcare.feed.model.data.remote

import android.util.Log
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecords
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import com.project.meongcare.feed.model.entities.Feeds
import org.json.JSONObject
import javax.inject.Inject

class FeedRemoteDataSource
    @Inject
    constructor() {
        private val feedApiService = FeedClient.feedService

        suspend fun postFeed(
            accessToken: String,
            feedUploadRequest: FeedUploadRequest,
        ): Int? {
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

        suspend fun getFeed(
            accessToken: String,
            dogId: Long,
        ): FeedGetResponse? {
            try {
                val getFeedResponse =
                    feedApiService.getFeed(
                        accessToken,
                        dogId,
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

        suspend fun getFeedPart(
            accessToken: String,
            dogId: Long,
            feedRecordId: Long,
        ): FeedPartRecords? {
            try {
                val getFeedPartResponse =
                    feedApiService.getFeedPart(
                        accessToken,
                        dogId,
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

        suspend fun getFeeds(
            accessToken: String,
            dogId: Long,
        ): Feeds? {
            try {
                val getFeedsResponse =
                    feedApiService.getFeeds(
                        accessToken,
                        dogId,
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

        suspend fun getPreviousFeed(
            accessToken: String,
            dogId: Long,
            feedRecordId: Long,
        ): FeedRecords? {
            try {
                val getPreviousFeedResponse =
                    feedApiService.getPreviousFeed(
                        accessToken,
                        dogId,
                        feedRecordId,
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

        suspend fun getFeedDetail(
            accessToken: String,
            feedId: Long,
            feedRecordId: Long,
        ): FeedDetailGetResponse? {
            try {
                val getFeedDetailResponse =
                    feedApiService.getDetailFeed(
                        accessToken,
                        feedId,
                        feedRecordId,
                    )

                if (getFeedDetailResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(getFeedDetailResponse.errorBody()?.string()!!)
                    Log.d("FeedDetailGetFailure", getFeedDetailResponse.code().toString())
                    Log.d("FeedDetailGetFailure", "$stringToJson")
                    return null
                }

                Log.d("FeedDetailGetSuccess", getFeedDetailResponse.code().toString())
                return getFeedDetailResponse.body()
            } catch (e: Exception) {
                Log.d("FeedDetailGetException", e.toString())
                return null
            }
        }

        suspend fun patchFeed(
            accessToken: String,
            feedPatchRequest: FeedPatchRequest,
        ): Int? {
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

        suspend fun putFeed(
            accessToken: String,
            feedUploadRequest: FeedUploadRequest,
        ): Int? {
            try {
                val putFeedResponse =
                    feedApiService.putFeed(
                        accessToken,
                        feedUploadRequest.dto,
                        feedUploadRequest.file,
                    )

                if (putFeedResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(putFeedResponse.code().toString())
                    Log.d("FeedPutFailure", putFeedResponse.code().toString())
                    Log.d("FeedPutFailure", "$stringToJson")
                    return null
                }

                Log.d("FeedPutSuccess", putFeedResponse.code().toString())
                return putFeedResponse.code()
            } catch (e: Exception) {
                Log.e("FeedPutException", e.toString())
                return null
            }
        }

        suspend fun deleteFeed(
            accessToken: String,
            feedRecordId: Long,
        ): Int? {
            try {
                val deleteFeedResponse =
                    feedApiService.deleteFeed(
                        accessToken,
                        feedRecordId,
                    )

                if (deleteFeedResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(deleteFeedResponse.errorBody()?.string()!!)
                    Log.d("FeedDeleteFailure", deleteFeedResponse.code().toString())
                    Log.d("FeedDeleteFailure", "$stringToJson")
                    return null
                }

                Log.d("FeedDeleteSuccess", deleteFeedResponse.code().toString())
                return deleteFeedResponse.code()
            } catch (e: Exception) {
                Log.e("FeedDeleteException", e.toString())
                return null
            }
        }
    }
