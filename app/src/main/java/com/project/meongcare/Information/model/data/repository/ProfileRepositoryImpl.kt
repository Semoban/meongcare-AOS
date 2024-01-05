package com.project.meongcare.Information.model.data.repository

import android.util.Log
import com.project.meongcare.Information.model.data.remote.ProfileRetrofitClient
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import javax.inject.Inject

class ProfileRepositoryImpl
    @Inject
    constructor(private val profileRetrofitClient: ProfileRetrofitClient): ProfileRepository {
        override suspend fun getUserProfile(accessToken: String): GetUserProfileResponse? {
            try {
                val response = profileRetrofitClient.profileApi.getUserProfile(accessToken)
                if (response.isSuccessful) {
                    Log.d("ProfileRepo-UserProfile", "통신 성공 : ${response.code()}")
                    return response.body()
                } else {
                    Log.d("ProfileRepo-UserProfile", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
