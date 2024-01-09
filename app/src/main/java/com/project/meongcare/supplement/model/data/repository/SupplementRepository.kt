package com.project.meongcare.supplement.model.data.repository

import com.project.meongcare.MainActivity
import com.project.meongcare.supplement.model.data.remote.RetrofitInstance
import com.project.meongcare.supplement.model.data.remote.SupplementAPI
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.DogSupplement
import com.project.meongcare.supplement.model.entities.RequestSupplement
import com.project.meongcare.supplement.model.entities.ResultSupplement
import okhttp3.ResponseBody

class SupplementRepository {

    companion object {
        private val supplementAPI: SupplementAPI =
            RetrofitInstance.getInstance().create(SupplementAPI::class.java)
    }

    suspend fun getSupplements(
        dogId: Int,
        date: String,
    ): Result<ResultSupplement> =
        kotlin.runCatching {
            val response = supplementAPI.getResultSupplement(
                MainActivity.ACCESS_TOKEN,
                date,
                dogId,
            )
            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Supplement get API 통신 실패")
            } else {
                throw RuntimeException("Supplement get API 통신 실패")
            }
        }

    suspend fun getSupplementDetail(
        supplementsId: Int,
    ): Result<DetailSupplement> =
        kotlin.runCatching {
            val response = supplementAPI.getSupplementDetail(
                MainActivity.ACCESS_TOKEN,
                supplementsId
            )
            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("영양제 상세조회 API 통신 실패")
            } else {
                throw RuntimeException("영양제 상세조회 API 통신 실패")
            }
        }

    suspend fun getSupplementDogs(
        dogId: Int,
    ): Result<DogSupplement> =
        kotlin.runCatching {
            val response = supplementAPI.getSupplementDogs(MainActivity.ACCESS_TOKEN, dogId)
            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Supplement get dog API 통신 실패")
            } else {
                throw RuntimeException("Supplement get dog API 통신 실패")
            }
        }

    suspend fun checkSupplement(supplementsRecordId: Int): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.checkSupplement(
                    MainActivity.ACCESS_TOKEN,
                    supplementsRecordId,
                )

            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Supplement check API 통신 실패")
            } else {
                throw RuntimeException("Supplement check API 통신 실패")
            }
        }

    suspend fun patchSupplementAlarm(
        supplementsId: Int,
        pushAgreement: Boolean,
    ): Result<ResponseBody> =
        kotlin.runCatching {
            val response = supplementAPI.patchSupplementAlarmStatus(
                MainActivity.ACCESS_TOKEN,
                supplementsId,
                pushAgreement,
            )

            if (response.isSuccessful) {
                response.body() ?: throw RuntimeException("Supplement alarm API returned no body.")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    "Supplement alarm API failed with code ${response.code()} and error: $errorBody"
                throw RuntimeException(errorMessage)
            }
        }

    suspend fun patchSupplementActive(
        supplementsId: Int,
        isActive: Boolean,
    ): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.patchSupplementActiveStatus(
                    MainActivity.ACCESS_TOKEN,
                    supplementsId,
                    isActive,
                )

            if (response.isSuccessful) {
                response.body() ?: throw RuntimeException("영양제 루틴 활성화 체크 API 통신 에러")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    "영양제 루틴 활성화 체크 API 통신 에러 코드 ${response.code()}, 전문: $errorBody"
                throw RuntimeException(errorMessage)
            }
        }

    suspend fun deleteSupplementsById(supplementsIds: IntArray): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.deleteSupplementsById(
                    MainActivity.ACCESS_TOKEN,
                    supplementsIds,
                )

            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Supplement delete API 통신 실패")
            } else {
                throw RuntimeException("Supplement delete API 통신 실패")
            }
        }

    suspend fun deleteSupplementById(supplementsId: Int): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.deleteSupplementById(
                    MainActivity.ACCESS_TOKEN,
                    supplementsId,
                )

            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("영양제 하나 삭제 API 통신 실패")
            } else {
                throw RuntimeException("영양제 하나 삭제 API 통신 실패")
            }
        }

    suspend fun addSupplement(requestSupplement: RequestSupplement): Int {
        val response =
            supplementAPI.addSupplement(
                MainActivity.ACCESS_TOKEN,
                requestSupplement.file,
                requestSupplement.dto,
            )
        return response.code()
    }
}

