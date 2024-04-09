package com.project.meongcare.excreta.model.entities

data class ExcretaPatchRequest(
    val excretaId: Long,
    val excretaString: String,
    val dateTime: String,
    val imageURL: String?,
)
