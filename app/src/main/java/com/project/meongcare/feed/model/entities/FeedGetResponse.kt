package com.project.meongcare.feed.model.entities

data class FeedGetResponse(
    val brand: String,
    val feedName: String,
    val protein: Double,
    val fat: Double,
    val crudeAsh: Double,
    val moisture: Double,
    val days: Long,
    val recommendIntake: Int,
    val feedId: Long,
    val feedRecordId: Long,
)
