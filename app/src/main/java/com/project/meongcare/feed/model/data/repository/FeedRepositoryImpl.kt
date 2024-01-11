package com.project.meongcare.feed.model.data.repository

import android.util.Log
import com.project.meongcare.feed.model.data.remote.FeedRemoteDataSource
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import javax.inject.Inject

class FeedRepositoryImpl
    @Inject
    constructor(
        private val feedRemoteDataSource: FeedRemoteDataSource,
    ) : FeedRepository {
        override suspend fun getFeed() = feedRemoteDataSource.getFeed()
        override suspend fun postFeed(feedUploadRequest: FeedUploadRequest) = feedRemoteDataSource.postFeed(feedUploadRequest)
        override suspend fun getFeedPart(feedRecordId: Long) = feedRemoteDataSource.getFeedPart(feedRecordId)

        override suspend fun getFeeds() = feedRemoteDataSource.getFeeds()

        override suspend fun getPreviousFeed(feedRecordId: Long) = feedRemoteDataSource.getPreviousFeed(feedRecordId)
    
        override suspend fun getDetailFeed(feedId: Long, feedRecordId: Long) = feedRemoteDataSource.getFeedDetail(feedId, feedRecordId)

        override suspend fun patchFeed(feedPatchRequest: FeedPatchRequest) = feedRemoteDataSource.patchFeed(feedPatchRequest)

        override suspend fun putFeed(feedUploadRequest: FeedUploadRequest) = feedRemoteDataSource.putFeed(feedUploadRequest)

        override suspend fun deleteFeed(feedId: Long) = feedRemoteDataSource.deleteFeed(feedId)
    }
