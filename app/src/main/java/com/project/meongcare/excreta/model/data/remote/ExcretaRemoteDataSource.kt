package com.project.meongcare.excreta.model.data.remote

import android.util.Log
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaPostRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaUploadRequest
import com.project.meongcare.excreta.utils.SUCCESS
import org.json.JSONObject
import javax.inject.Inject

class ExcretaRemoteDataSource
    @Inject
    constructor() {
        private val excretaApiService = ExcretaClient.excretaService

        suspend fun postExcreta(
            accessToken: String,
            excretaPostRequest: ExcretaPostRequest,
        ): Int? {
            try {
                val postResponse =
                    excretaApiService.postExcreta(
                        accessToken,
                        excretaPostRequest,
                    )

                if (postResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(postResponse.errorBody()?.string()!!)
                    Log.d("ExcretaPostFailure", postResponse.code().toString())
                    Log.d("ExcretaPostFailure", "$stringToJson")
                    return null
                }

                Log.d("ExcretaPostSuccess", postResponse.code().toString())
                return postResponse.code()
            } catch (e: Exception) {
                Log.e("ExcretaPostException", e.toString())
                return null
            }
        }

        suspend fun getExcretaRecord(
            accessToken: String,
            excretaRecordGetRequest: ExcretaRecordGetRequest,
        ): ExcretaRecordGetResponse? {
            try {
                val getExcretaRecordResponse =
                    excretaApiService.getExcretaRecords(
                        accessToken,
                        excretaRecordGetRequest.dogId,
                        excretaRecordGetRequest.dateTime,
                    )
                return if (getExcretaRecordResponse.code() == SUCCESS) {
                    Log.d("ExcretaRecordGetSuccess", getExcretaRecordResponse.code().toString())
                    getExcretaRecordResponse.body()
                } else {
                    val stringToJson = JSONObject(getExcretaRecordResponse.errorBody()?.string()!!)
                    Log.d("ExcretaRecordGetFailure", getExcretaRecordResponse.code().toString())
                    Log.d("ExcretaRecordGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("ExcretaRecordGetException", e.toString())
                return null
            }
        }

        suspend fun getExcretaDetail(
            accessToken: String,
            excretaId: Long,
        ): ExcretaDetailGetResponse? {
            try {
                val getExcretaDetailResponse =
                    excretaApiService.getExcretaDetail(
                        accessToken,
                        excretaId,
                    )
                return if (getExcretaDetailResponse.code() == SUCCESS) {
                    Log.d("ExcretaDetailGetSuccess", getExcretaDetailResponse.code().toString())
                    getExcretaDetailResponse.body()
                } else {
                    val stringToJson = JSONObject(getExcretaDetailResponse.errorBody()?.string()!!)
                    Log.d("ExcretaDetailGetFailure", getExcretaDetailResponse.code().toString())
                    Log.d("ExcretaDetailGetFailure", "$stringToJson")
                    null
                }
            } catch (e: Exception) {
                Log.e("ExcretaDetailGetException", e.toString())
                return null
            }
        }

        suspend fun deleteExcreta(
            accessToken: String,
            excretaIds: IntArray,
        ): Int? {
            try {
                val deleteExcretaResponse =
                    excretaApiService.deleteExcreta(
                        accessToken,
                        excretaIds,
                    )

                if (deleteExcretaResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(deleteExcretaResponse.errorBody()?.string()!!)
                    Log.d("ExcretaDeleteFailure", deleteExcretaResponse.code().toString())
                    Log.d("ExcretaDeleteFailure", "$stringToJson")
                    return null
                }

                Log.d("ExcretaDeleteSuccess", deleteExcretaResponse.code().toString())
                return deleteExcretaResponse.code()
            } catch (e: Exception) {
                Log.e("ExcretaDeleteException", e.toString())
                return null
            }
        }

        suspend fun patchExcreta(
            accessToken: String,
            excretaUploadRequest: ExcretaUploadRequest,
        ): Int? {
            try {
                val patchResponse =
                    excretaApiService.patchExcreta(
                        accessToken,
                        excretaUploadRequest.dto,
                        excretaUploadRequest.file,
                    )

                if (patchResponse.code() != SUCCESS) {
                    val stringToJson = JSONObject(patchResponse.errorBody()?.string()!!)
                    Log.d("ExcretaPatchFailure", patchResponse.code().toString())
                    Log.d("ExcretaPatchFailure", "$stringToJson")
                    return null
                }

                Log.d("ExcretaPatchSuccess", patchResponse.code().toString())
                return patchResponse.code()
            } catch (e: Exception) {
                Log.e("ExcretaPatchException", e.toString())
                return null
            }
        }
    }
