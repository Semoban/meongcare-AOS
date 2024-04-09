package com.project.meongcare.info.model.entities

data class DogPutRequest(
    val name: String,
    val type: String,
    val sex: String,
    val birthDate: String,
    val castrate: Boolean,
    val weight: Double,
    val backRound: Double?,
    val neckRound: Double?,
    val chestRound: Double?,
    val imageURL: String?,
)
