package com.project.meongcare.supplement.model.entities

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ResultSupplement(
    val routines: List<Supplement>,
)

data class InfoSupplement(
    val supplementsInfos: List<SupplementInfo>,
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

data class SupplementInfo(
    val supplementsId: Int,
    val name: String,
    val pushAgreement: Boolean,
)

