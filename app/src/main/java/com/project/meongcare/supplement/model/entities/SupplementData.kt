package com.project.meongcare.supplement.model.entities

data class ResultSupplement(
    val routines: List<Supplement>,
)

data class DogSupplement(
    val supplementsInfos: List<SupplementDog>,
)

data class Supplement(
    val supplementsRecordId: Int,
    val name: String,
    val intakeTime: String,
    val intakeCount: Int,
    val intakeUnit: String,
    val intakeStatus: Boolean,
)

data class IntakeInfo(
    val intakeTime: String,
    val intakeCount: Int
)

data class SupplementDto(
    val dogId: Int,
    val brand: String,
    val name: String,
    val intakeCycle: Int,
    val intakeUnit: String,
    val intakeInfos: List<IntakeInfo>
)

data class SupplementDog(
    val supplementsId: Int,
    val name: String,
    val pushAgreement: Boolean,
)

