package com.project.meongcare.weight.model.entities

data class WeightPostRequest(
    val dogId: Long,
    val date: String,
    val kg: Double,
)
