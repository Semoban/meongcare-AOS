package com.project.meongcare.medicalrecord.model.data.repository

import android.util.Log
import com.project.meongcare.medicalrecord.model.data.remote.MedicalRecordRetrofitClient
import com.project.meongcare.medicalrecord.model.entities.MedicalRecordGetResponse
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class MedicalRecordRepositoryImpl
    @Inject
    constructor(private val medicalRecordRetrofitClient: MedicalRecordRetrofitClient) : MedicalRecordRepository {
        override suspend fun getMedicalRecordList(
            dogId: Long,
            dateTime: String,
            accessToken: String,
        ): Response<MedicalRecordGetResponse>? {
            return try {
                val response = medicalRecordRetrofitClient.medicalRecordApi.getMedicalRecordList(dogId, dateTime, accessToken)
                if (response.isSuccessful) {
                    Log.d("MedicalRepo-GetList", "통신 성공 : ${response.code()}")
                    response
                } else {
                    Log.d("MedicalRepo-GetList", "통신 실패 : ${response.code()}")
                    response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun deleteMedicalRecordList(
            medicalRecordIds: IntArray,
            accessToken: String,
        ): Int? {
            return try {
                val response = medicalRecordRetrofitClient.medicalRecordApi.deleteMedicalRecordList(medicalRecordIds, accessToken)
                if (response.isSuccessful) {
                    Log.d("MedicalRepo-DeleteList", "통신 성공 : ${response.code()}")
                    response.code()
                } else {
                    val stringToJson = JSONObject(response.errorBody()?.string())
                    Log.d("MedicalRepo-DeleteList", "통신 실패 : ${response.code()} \n${stringToJson}")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
