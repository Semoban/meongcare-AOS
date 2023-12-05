package com.project.meongcare.login.model.entities

data class LoginRequest(
    val providerId: String,
    val provider: String,
    val name: String,
    val email: String,
    val profileImageUrl: String,
    val fcmToken: String,
)
