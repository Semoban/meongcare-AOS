package com.project.meongcare.medicalrecord.model.entities

data class MedicalRecord(
    val medicalRecordId: Long,
    val dateTime: String,
    var isChecked: Boolean = false,
)
