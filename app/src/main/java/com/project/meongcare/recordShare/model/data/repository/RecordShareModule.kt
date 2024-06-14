package com.project.meongcare.recordShare.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RecordShareModule {
    @Provides
    fun provideRecordShareRepository(recordShareRepositoryImpl: RecordShareShareRepositoryImpl): RecordShareRepository {
        return recordShareRepositoryImpl
    }
}
