package com.project.meongcare.symptom.model

data class ResultSymptom(
    val records : List<Symptom>
)

data class Symptom(
    val symptomId:Long,
    val dataTime: String,
    val symptomString: String,
    val note:String
)