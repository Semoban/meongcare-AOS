package com.project.meongcare.excreta.model.entities

data class ExcretaPostRequest(
    val dogId: Long,
    val excreta: String,
    val dateTime: String,
    val imageURL: String?,
)
