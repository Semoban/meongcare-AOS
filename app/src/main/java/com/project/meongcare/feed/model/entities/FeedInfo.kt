package com.project.meongcare.feed.model.entities

data class FeedInfo(
    val dogId: Long,
    val brand: String,
    val feedName: String,
    val protein: Double,
    val fat: Double,
    val crudeAsh: Double,
    val moisture: Double,
    val kcal: Double,
    val recommendIntake: Int,
    val startDate: Int,
    val endDate: Int,
)
