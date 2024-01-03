package com.project.meongcare.home.model.entities

data class HomeGetSymptomResponse(
    val symptomRecords: MutableList<Symptom>,
)

data class Symptom(
    val symptomString: String,
    val note: String,
)
