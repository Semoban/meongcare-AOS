package com.project.meongcare.info.model.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetDogInfoResponse(
    val dogId: Long,
    val name: String,
    val imageUrl: String?,
    val type: String,
    val sex: String,
    val castrate: Boolean,
    val birthDate: String,
    val backRound: Double?,
    val neckRound: Double?,
    val chestRound: Double?,
    val weight: Double,
) : Parcelable
