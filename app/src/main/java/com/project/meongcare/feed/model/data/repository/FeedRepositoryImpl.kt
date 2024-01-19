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
        override suspend fun getFeed(
            accessToken: String,
            dogId: Long,
        ) = feedRemoteDataSource.getFeed(accessToken, dogId)

        override suspend fun postFeed(
            accessToken: String,
            feedUploadRequest: FeedUploadRequest,
        ) = feedRemoteDataSource.postFeed(accessToken, feedUploadRequest)

        override suspend fun getFeedPart(
            accessToken: String,
            dogId: Long,
            feedRecordId: Long,
        ) = feedRemoteDataSource.getFeedPart(accessToken, dogId, feedRecordId)

        override suspend fun getFeeds(
            accessToken: String,
            dogId: Long,
        ) = feedRemoteDataSource.getFeeds(accessToken, dogId)

        override suspend fun getPreviousFeed(
            accessToken: String,
            dogId: Long,
            feedRecordId: Long,
        ) = feedRemoteDataSource.getPreviousFeed(accessToken, dogId, feedRecordId)

        override suspend fun getDetailFeed(
            accessToken: String,
            feedId: Long,
            feedRecordId: Long,
        ) = feedRemoteDataSource.getFeedDetail(accessToken, feedId, feedRecordId)

        override suspend fun patchFeed(
            accessToken: String,
            feedPatchRequest: FeedPatchRequest,
        ) = feedRemoteDataSource.patchFeed(accessToken, feedPatchRequest)

        override suspend fun putFeed(
            accessToken: String,
            feedUploadRequest: FeedUploadRequest,
        ) = feedRemoteDataSource.putFeed(accessToken, feedUploadRequest)

        override suspend fun deleteFeed(
            accessToken: String,
            feedId: Long,
        ) = feedRemoteDataSource.deleteFeed(accessToken, feedId)
    }
