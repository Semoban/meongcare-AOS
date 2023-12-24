package com.project.meongcare.supplement.model.entities

data class ResultSupplement(
    val routines: List<Supplement>,
)

data class ResponseSupplement(
    val success: Boolean,
    val message: String,
)

data class Supplement(
    val supplementsRecordId: Int,
    val name: String,
    val intakeTime: String,
    val intakeCount: Int,
    val intakeUnit: String,
    val intakeStatus: Boolean,
)

