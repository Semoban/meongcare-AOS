package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecords
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import com.project.meongcare.feed.model.entities.Feeds

interface FeedRepository {
    suspend fun getFeed(dogId: Long): FeedGetResponse?

    suspend fun postFeed(feedUploadRequest: FeedUploadRequest): Int?

    suspend fun getFeedPart(feedRecordId: Long): FeedPartRecords?

    suspend fun getFeeds(dogId: Long): Feeds?

    suspend fun getPreviousFeed(
        dogId: Long,
        feedRecordId: Long
    ): FeedRecords?

    suspend fun getDetailFeed(
        feedId: Long,
        feedRecordId: Long,
    ): FeedDetailGetResponse?

    suspend fun patchFeed(feedPatchRequest: FeedPatchRequest): Int?

    suspend fun putFeed(feedUploadRequest: FeedUploadRequest): Int?

    suspend fun deleteFeed(feedId: Long): Int?
}
