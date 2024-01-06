package com.project.meongcare.feed.model.data.repository

import com.project.meongcare.feed.model.entities.FeedGetResponse

interface FeedRepository {
    suspend fun getFeed(): FeedGetResponse?

    suspend fun postFeed(): Int?
}
