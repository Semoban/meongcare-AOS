package com.project.meongcare.excreta.model.data.repository

import com.project.meongcare.excreta.model.data.remote.ExcretaRemoteDataSource
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import javax.inject.Inject

class ExcretaRepositoryImpl
    @Inject
    constructor(
        private val excretaRemoteDataSource: ExcretaRemoteDataSource,
    ) : ExcretaRepository {
        override suspend fun getExcretaRecord(excretaRecordGetRequest: ExcretaRecordGetRequest) =
            excretaRemoteDataSource.getExcretaRecord(excretaRecordGetRequest)

        override suspend fun getExcretaDetail(excretaId: Long) =
            excretaRemoteDataSource.getExcretaDetail(excretaId)
    }
