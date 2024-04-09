package com.project.meongcare.medicalRecord.model.entities

data class MedicalRecordGet(
    val medicalRecordId: Long,
    val dateTime: String,
    val hospitalName: String,
    val doctorName: String,
    val note: String,
    val imageUrl: String,
)
