package com.project.meongcare.excreta.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExcretaModule {
    @Provides
    @Singleton
    fun provideExcretaRepository(excretaRepositoryImpl: ExcretaRepositoryImpl): ExcretaRepository {
        return excretaRepositoryImpl
    }
}
