package com.project.meongcare.login.model.entities

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val providerId: String,
    val provider: String,
    val name: String,
    val email: String,
    val profileImageUrl: String
)