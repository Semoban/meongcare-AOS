package com.project.meongcare.supplement.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SupplementModule {
    @Provides
    fun provideSupplementRepository(supplementRepositoryImpl: SupplementRepositoryImpl): SupplementRepository {
        return supplementRepositoryImpl
    }
}
