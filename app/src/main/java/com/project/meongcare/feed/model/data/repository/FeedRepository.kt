package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedRecords
import com.project.meongcare.feed.model.entities.Feeds

interface FeedRepository {
    suspend fun getFeed(): FeedGetResponse?

    suspend fun postFeed(): Int?

    suspend fun getFeedPart(feedRecordId: Long): FeedRecords?

    suspend fun getPreviousFeed(feedRecordId: Long): FeedRecords?

    suspend fun getFeeds(): Feeds?

    suspend fun patchFeed(): Int?
}
