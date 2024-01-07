package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.data.remote.FeedRemoteDataSource
import javax.inject.Inject

class FeedRepositoryImpl
    @Inject
    constructor(
        private val feedRemoteDataSource: FeedRemoteDataSource,
    ) : FeedRepository {
        override suspend fun getFeed() = feedRemoteDataSource.getFeed()
        override suspend fun postFeed() = feedRemoteDataSource.postFeed()
        override suspend fun getFeedPart(feedRecordId: Long) = feedRemoteDataSource.getFeedPart(feedRecordId)

        override suspend fun getPreviousFeed(feedRecordId: Long) = feedRemoteDataSource.getPreviousFeed(feedRecordId)

        override suspend fun getFeeds() = feedRemoteDataSource.getFeeds()

        override suspend fun patchFeed() = feedRemoteDataSource.patchFeed()
    }
