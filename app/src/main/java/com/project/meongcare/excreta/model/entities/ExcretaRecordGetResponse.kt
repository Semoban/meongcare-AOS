package com.project.meongcare.excreta.model.entities

data class ExcretaRecordGetResponse(
    val fecesCount: Int,
    val urineCount: Int,
    val excretaRecords: List<ExcretaRecord>,
)
