package com.project.meongcare.medicalrecord.model.data.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MedicalRecordModule {
    @Provides
    fun provideMedicalRecordRepository(medicalRecordRepositoryImpl: MedicalRecordRepositoryImpl): MedicalRecordRepository {
        return medicalRecordRepositoryImpl
    }
}
