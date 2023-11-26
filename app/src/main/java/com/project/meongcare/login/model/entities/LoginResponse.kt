package com.project.meongcare.login.model.entities

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)