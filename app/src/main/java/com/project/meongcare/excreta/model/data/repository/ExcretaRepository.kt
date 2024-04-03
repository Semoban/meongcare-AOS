package com.project.meongcare.excreta.model.data.repository

import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaPostRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaUploadRequest

interface ExcretaRepository {
    suspend fun postExcreta(
        accessToken: String,
        excretaPostRequest: ExcretaPostRequest,
    ): Int?

    suspend fun getExcretaRecord(
        accessToken: String,
        excretaRecordGetRequest: ExcretaRecordGetRequest,
    ): ExcretaRecordGetResponse?

    suspend fun getExcretaDetail(
        accessToken: String,
        excretaId: Long,
    ): ExcretaDetailGetResponse?

    suspend fun deleteExcreta(
        accessToken: String,
        excretaIds: IntArray,
    ): Int?

    suspend fun patchExcreta(
        accessToken: String,
        excretaUploadRequest: ExcretaUploadRequest,
    ): Int?
}
