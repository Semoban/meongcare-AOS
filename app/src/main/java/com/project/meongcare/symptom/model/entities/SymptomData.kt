package com.project.meongcare.symptom.model.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ResultSymptom(
    val records: List<Symptom>,
)

data class ToAddSymptom(
    val dogId: Int,
    val symptomString: String,
    val note: String,
    val dateTime: String,
)

data class ToEditSymptom(
    val symptomId: Int,
    val dateTime: String,
    val symptomString: String,
    val note: String,
)

@Parcelize
data class Symptom(
    val symptomId: Int,
    val dateTime: String,
    val symptomString: String,
    val note: String
) : Parcelable

data class SymptomItem(
    val type: SymptomType,
    val title: String,
    val img: Int,
)

