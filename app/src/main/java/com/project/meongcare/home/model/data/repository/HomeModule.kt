package com.project.meongcare.home.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    fun provideHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository {
        return homeRepositoryImpl
    }
}
