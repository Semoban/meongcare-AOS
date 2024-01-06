package com.project.meongcare.Information.model.data.repository

import android.util.Log
import com.project.meongcare.Information.model.data.remote.ProfileRetrofitClient
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import javax.inject.Inject

class ProfileRepositoryImpl
    @Inject
    constructor(private val profileRetrofitClient: ProfileRetrofitClient): ProfileRepository {
        override suspend fun getUserProfile(accessToken: String): GetUserProfileResponse? {
            try {
                val response = profileRetrofitClient.profileApi.getUserProfile(accessToken)
                return if (response.isSuccessful) {
                    Log.d("ProfileRepo-UserProfile", "통신 성공 : ${response.code()}")
                    response.body()
                } else {
                    Log.d("ProfileRepo-UserProfile", "통신 실패 : ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override suspend fun getDogList(accessToken: String): MutableList<DogProfile>? {
            try {
                val response = profileRetrofitClient.profileApi.getDogList(accessToken)
                return if (response.isSuccessful) {
                    Log.d("ProfileRepo-DogList", "통신 성공 : ${response.code()}")
                    response.body()?.dogs
                } else {
                    Log.d("ProfileRepo-DogList", "통신 실패 : ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
