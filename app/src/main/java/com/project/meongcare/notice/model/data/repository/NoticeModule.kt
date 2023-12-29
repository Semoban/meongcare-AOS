package com.project.meongcare.notice.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NoticeModule {
    @Provides
    fun provideNoticeRepository(noticeRepositoryImpl: NoticeRepositoryImpl): NoticeRepository {
        return noticeRepositoryImpl
    }
}
