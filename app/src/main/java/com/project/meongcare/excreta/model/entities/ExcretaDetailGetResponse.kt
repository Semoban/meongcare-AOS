package com.project.meongcare.excreta.model.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExcretaDetailGetResponse(
    val excretaImageURL: String?,
    val dateTime: String,
    val excretaType: String,
) : Parcelable
