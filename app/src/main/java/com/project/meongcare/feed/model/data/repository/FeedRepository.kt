package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecords

interface FeedRepository {
    suspend fun getFeed(): FeedGetResponse?

    suspend fun postFeed(): Int?

    suspend fun getFeedPart(feedRecordId: Long): FeedPartRecords?
}
