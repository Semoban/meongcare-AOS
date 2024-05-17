package com.project.meongcare.aws.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AWSS3Module {
    @Provides
    fun provideAWSS3Repository(awsS3RepositoryImpl: AWSS3RepositoryImpl): AWSS3Repository {
        return awsS3RepositoryImpl
    }
}
