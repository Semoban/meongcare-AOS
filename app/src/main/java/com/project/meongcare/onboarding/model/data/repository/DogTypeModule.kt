package com.project.meongcare.onboarding.model.data.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DogTypeModule {
    @Provides
    @Singleton
    fun provideContext(
        @ApplicationContext context: Context,
    ): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideDogtypeRepository(context: Context): DogTypeRepository {
        return DogTypeRepository(context)
    }
}
