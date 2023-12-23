package com.project.meongcare.excreta.model.data.repository

import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse

interface ExcretaRepository {
    suspend fun getExcretaRecord(excretaRecordGetRequest: ExcretaRecordGetRequest): ExcretaRecordGetResponse?
}
