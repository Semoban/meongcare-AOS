package com.project.meongcare.feed.model.entities

data class FeedRecord(
    val brandName: String,
    val feedName: String,
    val startDate: String,
    val endDate: String,
    val feedImageURL: String,
    val feedRecordId: Long,
)
