package com.project.meongcare.supplement.model.data.repository

import com.project.meongcare.supplement.model.data.remote.SupplementAPI
import com.project.meongcare.supplement.model.data.remote.SupplementRetrofitInstance
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.DogSupplement
import com.project.meongcare.supplement.model.entities.RequestSupplement
import com.project.meongcare.supplement.model.entities.ResultSupplement
import okhttp3.ResponseBody
import javax.inject.Inject

class SupplementRepository
@Inject
constructor() {
    companion object {
        private val supplementAPI: SupplementAPI =
            SupplementRetrofitInstance.getInstance().create(SupplementAPI::class.java)
    }

    suspend fun getSupplements(
        accessToken: String?,
        dogId: Long?,
        date: String,
    ): Result<ResultSupplement> =
        kotlin.runCatching {
            val response =
                supplementAPI.getResultSupplement(
                    accessToken,
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
        accessToken: String?,
        supplementsId: Int
    ): Result<DetailSupplement> =
        kotlin.runCatching {
            val response =
                supplementAPI.getSupplementDetail(
                    accessToken,
                    supplementsId,
                )
            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("영양제 상세조회 API 통신 실패")
            } else {
                throw RuntimeException("영양제 상세조회 API 통신 실패")
            }
        }

    suspend fun getSupplementDogs(
        accessToken: String?,
        dogId: Long?
    ): Result<DogSupplement> =
        kotlin.runCatching {
            val response = supplementAPI.getSupplementDogs(accessToken, dogId)
            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Supplement get dog API 통신 실패")
            } else {
                throw RuntimeException("Supplement get dog API 통신 실패")
            }
        }

    suspend fun checkSupplement(
        accessToken: String?,
        supplementsRecordId: Int
    ): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.checkSupplement(
                    accessToken,
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
        accessToken: String?,
        supplementsId: Int,
        pushAgreement: Boolean,
    ): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.patchSupplementAlarmStatus(
                    accessToken,
                    supplementsId,
                    pushAgreement,
                )

            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Supplement alarm API returned no body.")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    "Supplement alarm API failed with code ${response.code()} and error: $errorBody"
                throw RuntimeException(errorMessage)
            }
        }

    suspend fun patchSupplementActive(
        accessToken: String?,
        supplementsId: Int,
        isActive: Boolean,
    ): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.patchSupplementActiveStatus(
                    accessToken,
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

    suspend fun deleteSupplementsById(
        accessToken: String?,
        supplementsIds: IntArray
    ): Int {
        val response =
            supplementAPI.deleteSupplementsById(
                accessToken,
                supplementsIds,
            )

        return response.code()
    }


    suspend fun deleteSupplementById(
        accessToken: String?,
        supplementsId: Int
    ): Int {
        val response =
            supplementAPI.deleteSupplementById(
                accessToken,
                supplementsId,
            )
        return response.code()
    }

    suspend fun addSupplement(
        accessToken: String?,
        requestSupplement: RequestSupplement
    ): Int {
        val response =
            supplementAPI.addSupplement(
                accessToken,
                requestSupplement.file,
                requestSupplement.dto,
            )
        return response.code()
    }
}


