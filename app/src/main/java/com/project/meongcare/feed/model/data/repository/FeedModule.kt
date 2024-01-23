package com.project.meongcare.feed.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {
    @Provides
    @Singleton
    fun provideFeedRepository(feedRepositoryImpl: FeedRepositoryImpl): FeedRepository {
        return feedRepositoryImpl
    }
}
