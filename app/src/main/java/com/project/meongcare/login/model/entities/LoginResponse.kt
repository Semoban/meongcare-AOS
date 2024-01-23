package com.project.meongcare.login.model.entities

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isFirstLogin: Boolean,
)

data class ReissueResponse(
    val accessToken: String,
)
