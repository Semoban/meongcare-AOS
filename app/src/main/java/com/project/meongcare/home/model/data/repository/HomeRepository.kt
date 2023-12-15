package com.project.meongcare.home.model.data.repository

import com.project.meongcare.home.model.entities.HomeProfileResponse

interface HomeRepository {
    suspend fun getUserProfile(accessToken: String): HomeProfileResponse?
}
