package com.project.meongcare.medicalRecord.model.data.repository

import android.util.Log
import com.project.meongcare.RetrofitClient
import com.project.meongcare.medicalRecord.model.data.remote.MedicalRecordApi
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordPutDto
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class MedicalRecordRepositoryImpl
    @Inject
    constructor(retrofitClient: RetrofitClient) : MedicalRecordRepository {
        private val medicalRecordApi = retrofitClient.createApi<MedicalRecordApi>()
        override suspend fun getMedicalRecordList(
            dogId: Long,
            dateTime: String,
            accessToken: String,
        ): Response<MedicalRecordGetResponse>? {
            return try {
                val response = medicalRecordApi.getMedicalRecordList(dogId, dateTime, accessToken)
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

        override suspend fun getMedicalRecord(
            medicalRecordId: Long,
            accessToken: String,
        ): Response<MedicalRecordGet>? {
            return try {
                val response = medicalRecordApi.getMedicalRecord(medicalRecordId, accessToken)
                if (response.isSuccessful) {
                    Log.d("MedicalRepo-Get", "통신 성공 : ${response.code()}")
                    response
                } else {
                    Log.d("MedicalRepo-Get", "통신 실패 : ${response.code()}")
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
                val response = medicalRecordApi.deleteMedicalRecordList(medicalRecordIds, accessToken)
                if (response.isSuccessful) {
                    Log.d("MedicalRepo-DeleteList", "통신 성공 : ${response.code()}")
                    response.code()
                } else {
                    val stringToJson = JSONObject(response.errorBody()?.string())
                    Log.d("MedicalRepo-DeleteList", "통신 실패 : ${response.code()} \n$stringToJson")
                    response.code()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override suspend fun addMedicalRecord(
            accessToken: String?,
            medicalRecordDto: MedicalRecordDto,
        ): Int {
            val response =
                medicalRecordApi.addMedicalRecord(
                    accessToken,
                    medicalRecordDto,
                )
            return response.code()
        }

        override suspend fun putMedicalRecord(
            accessToken: String?,
            medicalRecordPutDto: MedicalRecordPutDto,
        ): Int {
            val response =
                medicalRecordApi.putMedicalRecord(
                    accessToken,
                    medicalRecordPutDto,
                )
            return response.code()
        }
    }
