package com.project.meongcare.supplement.model.entities

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ResultSupplement(
    val routines: List<Supplement>,
)

data class RequestSupplement(
    val dto: RequestBody,
    val file: MultipartBody.Part,
)

data class DogSupplement(
    val supplementsInfos: List<SupplementDog>,
)

data class Supplement(
    val supplementsRecordId: Int,
    val supplementsId: Int,
    val name: String,
    val intakeTime: String,
    val intakeCount: Int,
    val intakeUnit: String,
    val intakeStatus: Boolean,
)

data class DetailSupplement(
    val supplementsId: Int,
    val imageUrl: String,
    val isActive: Boolean,
    val brand: String,
    val name: String,
    val intakeCycle: Int,
    val intakeUnit: String,
    val intakeInfos: List<IntakeInfo>,
)

data class SupplementPostRequest(
    val dogId: Long,
    val brand: String,
    val name: String,
    val intakeCycle: Int,
    val intakeUnit: String,
    val imageURL: String?,
    val intakeInfos: List<IntakeInfo>,
)

data class IntakeInfo(
    val intakeTime: String,
    val intakeCount: Int,
)

data class SupplementDog(
    val supplementsId: Int,
    val name: String,
    val pushAgreement: Boolean,
)

