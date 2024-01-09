package com.project.meongcare.home.model.entities

data class GetDogListResponse(
    val dogs: MutableList<DogProfile>,
)

data class DogProfile(
    val dogId: Long,
    val name: String,
    val imageUrl: String,
)
