package com.project.meongcare.excreta.model.data.repository

import com.project.meongcare.excreta.model.data.remote.ExcretaRemoteDataSource
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import com.project.meongcare.excreta.model.entities.ExcretaUploadRequest
import javax.inject.Inject

class ExcretaRepositoryImpl
    @Inject
    constructor(
        private val excretaRemoteDataSource: ExcretaRemoteDataSource,
    ) : ExcretaRepository {
        override suspend fun postExcreta(
            accessToken: String,
            excretaUploadRequest: ExcretaUploadRequest,
        ) = excretaRemoteDataSource.postExcreta(accessToken, excretaUploadRequest)

        override suspend fun getExcretaRecord(
            accessToken: String,
            excretaRecordGetRequest: ExcretaRecordGetRequest,
        ) = excretaRemoteDataSource.getExcretaRecord(accessToken, excretaRecordGetRequest)

        override suspend fun getExcretaDetail(
            accessToken: String,
            excretaId: Long,
        ) = excretaRemoteDataSource.getExcretaDetail(accessToken, excretaId)

        override suspend fun deleteExcreta(
            accessToken: String,
            excretaIds: IntArray,
        ) = excretaRemoteDataSource.deleteExcreta(accessToken, excretaIds)

        override suspend fun patchExcreta(
            accessToken: String,
            excretaUploadRequest: ExcretaUploadRequest,
        ) = excretaRemoteDataSource.patchExcreta(accessToken, excretaUploadRequest)
    }
