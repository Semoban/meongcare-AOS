package com.project.meongcare.symptom.model.entities

import java.time.LocalDateTime

data class ResultSymptom(
    val records: List<Symptom>,
)

data class ResponseSymptom(
    val success: Boolean,
    val message: String,
)

data class ToAddSymptom(
    val dogId: Int,
    val symptomString: String,
    val note: String,
    val dateTime: LocalDateTime,
)

data class Symptom(
    val symptomId: Int,
    val dateTime: String,
    val symptomString: String,
    val note: String,
)

data class SymptomItem(
    val type: SymptomType,
    val title: String,
    val img: Int,
)

