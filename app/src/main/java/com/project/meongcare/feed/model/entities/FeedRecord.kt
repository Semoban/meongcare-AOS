package com.project.meongcare.feed.model.entities

data class FeedRecord(
    val brand: String,
    val feedName: String,
    val startDate: String,
    val endDate: String,
    val feedRecordId: Long,
    val imageURL: String,
)
