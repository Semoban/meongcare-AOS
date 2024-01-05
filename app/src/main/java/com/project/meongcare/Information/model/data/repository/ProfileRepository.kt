package com.project.meongcare.Information.model.data.repository

import com.project.meongcare.home.model.entities.GetUserProfileResponse

interface ProfileRepository {
    suspend fun getUserProfile(accessToken: String): GetUserProfileResponse?
}
