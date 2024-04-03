package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecords
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedPostRequest
import com.project.meongcare.feed.model.entities.FeedPutRequest
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.Feeds

interface FeedRepository {
    suspend fun getFeed(
        accessToken: String,
        dogId: Long,
    ): FeedGetResponse?

    suspend fun postFeed(
        accessToken: String,
        feedPostRequest: FeedPostRequest,
    ): Int?

    suspend fun getFeedPart(
        accessToken: String,
        dogId: Long,
        feedRecordId: Long,
    ): FeedPartRecords?

    suspend fun getFeeds(
        accessToken: String,
        dogId: Long,
    ): Feeds?

    suspend fun getPreviousFeed(
        accessToken: String,
        dogId: Long,
        feedRecordId: Long,
    ): FeedRecords?

    suspend fun getDetailFeed(
        accessToken: String,
        feedId: Long,
        feedRecordId: Long,
    ): FeedDetailGetResponse?

    suspend fun patchFeed(
        accessToken: String,
        feedPatchRequest: FeedPatchRequest,
    ): Int?

    suspend fun stopFeed(
        accessToken: String,
        feedRecordId: Long,
    ): Int?

    suspend fun putFeed(
        accessToken: String,
        feedPutRequest: FeedPutRequest,
    ): Int?

    suspend fun deleteFeed(
        accessToken: String,
        feedId: Long,
    ): Int?
}
