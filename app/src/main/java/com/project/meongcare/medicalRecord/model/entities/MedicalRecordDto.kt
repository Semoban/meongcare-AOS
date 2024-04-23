package com.project.meongcare.medicalRecord.model.entities

data class MedicalRecordDto(
    val dogId: Long,
    val dateTime: String,
    val hospitalName: String,
    val doctorName: String,
    val note: String,
    val imageURL: String?,
)
