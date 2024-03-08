package com.project.meongcare.medicalrecord.model.entities

data class MedicalRecordGetResponse(
    val records: MutableList<MedicalRecordListItem>
)

data class MedicalRecordListItem(
    val medicalRecordId: Long,
    val dateTime: String,
)
