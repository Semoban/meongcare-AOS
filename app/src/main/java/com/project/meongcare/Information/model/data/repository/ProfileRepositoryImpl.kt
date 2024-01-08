package com.project.meongcare.Information.model.data.repository

import android.util.Log
import com.project.meongcare.Information.model.data.remote.ProfileRetrofitClient
import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.GetUserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

        override suspend fun getdogInfo(
            dogId: Long,
            accessToken: String,
        ): GetDogInfoResponse? {
            return try {
                val response = profileRetrofitClient.profileApi.getDogInfo(dogId, accessToken)
                if (response.isSuccessful) {
                    Log.d("ProfileRepo-DogInfo", "통신 성공 : ${response.code()}")
                    response.body()
                } else {
                    Log.d("ProfileRepo-DogInfo", "통신 실패 : ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun deleteDog(
            dogId: Long,
            accessToken: String,
        ): Int? {
            return try {
                val response = profileRetrofitClient.profileApi.deleteDog(dogId, accessToken)
                if (response.code() == 200) {
                    Log.d("ProfileRepo-DeleteDog", "통신 성공 : ${response.code()}")
                    response.code()
                } else {
                    Log.d("ProfileRepo-DeleteDog", "통신 실패 : ${response.code()}")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun putDogInfo(
            dogId: Long,
            accessToken: String,
            file: MultipartBody.Part,
            dto: RequestBody
        ): Int? {
            return try {
                val response = profileRetrofitClient.profileApi.putDogInfo(dogId, accessToken, file, dto)
                if (response.code() == 200) {
                    Log.d("ProfileRepo-PutDog", "통신 성공 : ${response.code()}")
                    response.code()
                } else {
                    Log.d("ProfileRepo-PutDog", "통신 실패 : ${response.code()}")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun logoutUser(refreshToken: String): Int? {
            return try {
                val response = profileRetrofitClient.profileApi.logoutUser(refreshToken)
                if (response.code() == 200) {
                    Log.d("ProfileRepo-Logout", "통신 성공")
                    response.code()
                } else {
                    Log.e("ProfileRepo-Logout", "통신 실패 : ${response.code()}")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun deleteUser(accessToken: String): Int? {
            return try {
                val response = profileRetrofitClient.profileApi.deleteUser(accessToken)
                if (response.code() == 200) {
                    Log.d("ProfileRepo-DeleteUser", "통신 성공")
                    response.code()
                } else {
                    Log.e("ProfileRepo-DeleteUser", "통신 실패 : ${response.code()}")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun patchPushAgreement(pushAgreement: Boolean, accessToken: String): Int? {
            return try {
                val response = profileRetrofitClient.profileApi.patchPushAgreement(pushAgreement, accessToken)
                if (response.code() == 200) {
                    Log.d("ProfileRepo-PatchPush", "통신 성공")
                    response.code()
                } else {
                    Log.e("ProfileRepo-PatchPush", "통신 실패 : ${response.code()}")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
