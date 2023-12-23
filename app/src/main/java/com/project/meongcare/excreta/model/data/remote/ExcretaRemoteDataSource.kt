package com.project.meongcare.excreta.model.data.remote

import android.util.Log
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

        companion object {
            const val SUCCESS = 200
        }
    }
