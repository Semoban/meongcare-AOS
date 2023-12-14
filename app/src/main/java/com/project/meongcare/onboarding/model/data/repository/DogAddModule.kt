package com.project.meongcare.onboarding.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DogAddModule {
    @Provides
    fun provideDogAddRepository(dogAddRepositoryImpl: DogAddRepositoryImpl): DogAddRepository {
        return dogAddRepositoryImpl
    }
}
