package com.project.meongcare.weight.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeightModule {
    @Provides
    @Singleton
    fun provideWeightRepository(weightRepositoryImpl: WeightRepositoryImpl): WeightRepository {
        return weightRepositoryImpl
    }
}
