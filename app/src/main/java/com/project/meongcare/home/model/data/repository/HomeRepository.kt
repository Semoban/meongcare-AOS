package com.project.meongcare.home.model.data.repository

import com.project.meongcare.home.model.entities.HomeGetProfileResponse

interface HomeRepository {
    suspend fun getUserProfile(accessToken: String): HomeGetProfileResponse?
}
