package com.project.meongcare.weight.model.entities

data class WeightPatchRequest(
    val dogId: Long,
    val kg: Double,
    val date: String,
)
