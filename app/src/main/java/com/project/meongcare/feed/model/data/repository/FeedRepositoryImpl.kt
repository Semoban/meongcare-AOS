package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.data.remote.FeedRemoteDataSource
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import javax.inject.Inject

class FeedRepositoryImpl
    @Inject
    constructor(
        private val feedRemoteDataSource: FeedRemoteDataSource,
    ) : FeedRepository {
        override suspend fun getFeed(dogId: Long) = feedRemoteDataSource.getFeed(dogId)

        override suspend fun postFeed(feedUploadRequest: FeedUploadRequest) = feedRemoteDataSource.postFeed(feedUploadRequest)

        override suspend fun getFeedPart(
            dogId: Long,
            feedRecordId: Long,
        ) = feedRemoteDataSource.getFeedPart(dogId, feedRecordId)

        override suspend fun getFeeds(dogId: Long) = feedRemoteDataSource.getFeeds(dogId)

        override suspend fun getPreviousFeed(
            dogId: Long,
            feedRecordId: Long
        ) = feedRemoteDataSource.getPreviousFeed(dogId, feedRecordId)

        override suspend fun getDetailFeed(
            feedId: Long,
            feedRecordId: Long,
        ) = feedRemoteDataSource.getFeedDetail(feedId, feedRecordId)

        override suspend fun patchFeed(feedPatchRequest: FeedPatchRequest) = feedRemoteDataSource.patchFeed(feedPatchRequest)

        override suspend fun putFeed(feedUploadRequest: FeedUploadRequest) = feedRemoteDataSource.putFeed(feedUploadRequest)

        override suspend fun deleteFeed(feedId: Long) = feedRemoteDataSource.deleteFeed(feedId)
    }
