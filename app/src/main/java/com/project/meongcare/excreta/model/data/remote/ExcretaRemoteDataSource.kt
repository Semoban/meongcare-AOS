package com.project.meongcare.excreta.model.data.remote

import android.util.Log
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetRequest
import com.project.meongcare.excreta.model.entities.ExcretaRecordGetResponse
import org.json.JSONObject
import javax.inject.Inject

class ExcretaRemoteDataSource
    @Inject
    constructor() {
        private val accessToken =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NiwiZXhwIjoxNzAzMzY1MjQ2fQ.qbSYeabyBpAni3yISWDUGYgFkQdKYfdFqPlMlz7DKCs"
        private val excretaApiService = ExcretaClient.excretaService

        suspend fun getExcretaRecord(excretaRecordGetRequest: ExcretaRecordGetRequest): ExcretaRecordGetResponse? {
            try {
                val getExcretaRecordResponse =
                    excretaApiService.getExcretaRecords(
                        accessToken,
                        excretaRecordGetRequest.excretaId,
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

        suspend fun getExcretaDetail(excretaId: Long): ExcretaDetailGetResponse? {
            try {
                val getExcretaDetailResponse =
                    excretaApiService.getExcretaDetail(
                        accessToken,
                        excretaId
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

        companion object {
            const val SUCCESS = 200
        }
    }
