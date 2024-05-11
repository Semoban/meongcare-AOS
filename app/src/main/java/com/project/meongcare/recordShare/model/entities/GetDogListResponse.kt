package com.project.meongcare.recordShare.model.entities

data class GetDogListResponse(
    val dogs: MutableList<DogProfile>,
)

data class DogProfile(
    val dogId: Long,
    val name: String,
    val imageUrl: String,
)
