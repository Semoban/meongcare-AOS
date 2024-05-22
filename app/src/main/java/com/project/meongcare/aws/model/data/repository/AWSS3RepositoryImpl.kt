package com.project.meongcare.aws.model.data.repository

import android.util.Log
import com.project.meongcare.RetrofitClient
import com.project.meongcare.aws.model.data.remote.AWSS3Api
import com.project.meongcare.aws.model.entities.AWSS3Response
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.Exception
import javax.inject.Inject

class AWSS3RepositoryImpl
    @Inject
    constructor(retrofitClient: RetrofitClient) : AWSS3Repository {
        private val awsS3Api = retrofitClient.createApi<AWSS3Api>()
        override suspend fun getPreSignedUrl(
            accessToken: String,
            fileName: String,
        ): AWSS3Response? {
            return try {
                val response = awsS3Api.getPreSignedUrl(accessToken, fileName)
                if (response.isSuccessful) {
                    Log.d("AWSS3Repo-getPreUrl", "통신 성공 ${response.code()}")
                    response.body()
                } else {
                    val stringToJson = JSONObject(response.errorBody()?.string()!!)
                    Log.e("AWSS3Repo-getPreUrl", "통신 실패 ${response.code()}\n$stringToJson")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun uploadImageToS3(
            preSignedUrl: String,
            image: RequestBody,
        ): Int? {
            return try {
                val response = awsS3Api.uploadImageToS3(preSignedUrl, image)
                if (response.isSuccessful) {
                    Log.d("AWSS3Repo-upload", "통신 성공 ${response.code()}")
                    response.code()
                } else {
                    val stringToJson = JSONObject(response.errorBody()?.string()!!)
                    Log.e("AWSS3Repo-upload", "통신 실패 ${response.code()}\n$stringToJson")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
