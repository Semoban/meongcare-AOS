package com.project.meongcare.medicalRecord.model.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicalRecordGet(
    val medicalRecordId: Long,
    val dateTime: String,
    val hospitalName: String,
    val doctorName: String,
    val note: String,
    val imageUrl: String?,
) : Parcelable
