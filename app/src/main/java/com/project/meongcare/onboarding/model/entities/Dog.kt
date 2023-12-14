package com.project.meongcare.onboarding.model.entities

data class Dog(
    val name: String,
    val type: String,
    val sex: String,
    val birthDate: String,
    val castrate: Boolean,
    val weight: Double,
    val backRound: Double?,
    val neckRound: Double?,
    val chestRound: Double?,
)
