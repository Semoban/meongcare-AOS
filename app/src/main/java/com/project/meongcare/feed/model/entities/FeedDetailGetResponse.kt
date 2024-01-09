package com.project.meongcare.feed.model.entities

data class FeedDetailGetResponse(
    val brand: String,
    val feedName: String,
    val protein: Double,
    val fat: Double,
    val crudeAsh: Double,
    val moisture: Double,
    val kcal: Double,
    val recommendIntake: Long,
    val imageURL: String,
    val startDate: String,
    val endDate: String,
)