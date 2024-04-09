package com.project.meongcare.feed.model.entities

data class FeedPostRequest(
    val dogId: Long,
    val brand: String,
    val feedName: String,
    val protein: Double,
    val fat: Double,
    val crudeAsh: Double,
    val moisture: Double,
    val etc: Double,
    val kcal: Double,
    val recommendIntake: Int,
    val startDate: String,
    val endDate: String? = null,
    val imageURL: String?,
)
