package com.project.meongcare.medicalRecord.model.data.repository

import android.util.Log
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.data.remote.MedicalRecordRetrofitClient
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import com.project.meongcare.medicalRecord.model.entities.RequestMedicalRecord
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.RequestSupplement
import okhttp3.ResponseBody
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

    override suspend fun getMedicalRecord(
        medicalRecordId: Long,
        accessToken: String
    ): Response<MedicalRecordGet>? {
        return try {
            val response = medicalRecordRetrofitClient.medicalRecordApi.getMedicalRecord(medicalRecordId, accessToken)
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
                val response = medicalRecordRetrofitClient.medicalRecordApi.deleteMedicalRecordList(medicalRecordIds, accessToken)
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
        requestSupplement: RequestMedicalRecord,
    ): Int {
        val response =
            medicalRecordRetrofitClient.medicalRecordApi.addMedicalRecord(
                accessToken,
                requestSupplement.file,
                requestSupplement.dto,
            )
        return response.code()
    }

}
