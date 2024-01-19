package com.project.meongcare.symptom.model.data.repository

import com.project.meongcare.symptom.model.data.remote.SymptomAPI
import com.project.meongcare.symptom.model.data.remote.SymptomRetrofitInstance
import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import okhttp3.ResponseBody
import javax.inject.Inject

class SymptomRepository
@Inject
constructor() {
    companion object {
        private val symptomAPI: SymptomAPI =
            SymptomRetrofitInstance.getInstance().create(SymptomAPI::class.java)
    }

    suspend fun getSymptomByDogId(
        accessToken: String?,
        dogId: Long?,
        dateTime: String,
    ): Result<ResultSymptom> =
        kotlin.runCatching {
            val response =
                symptomAPI.getSymptomList(
                    accessToken,
                    dogId,
                    dateTime,
                )
            if (response.isSuccessful) {
                response.body()
                    ?: throw RuntimeException("Symptom get API 통신 실패")
            } else {
                throw RuntimeException("Symptom get API 통신 실패")
            }
        }

    suspend fun addSymptom(
        accessToken: String?,
        toAddSymptom: ToAddSymptom,
    ): Int {
        val response =
            symptomAPI.addSymptom(
                accessToken,
                toAddSymptom,
            )
        return response.code()
    }

    suspend fun deleteSymptom(
        accessToken: String?,
        symptomIds: IntArray,
    ): Int {
        val response =
            symptomAPI.deleteSymptom(
                accessToken,
                symptomIds,
            )
        return response.code()
    }

    suspend fun patchSymptom(
        accessToken: String?,
        toEditSymptom: ToEditSymptom,
    ): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                symptomAPI.patchSymptom(
                    accessToken,
                    toEditSymptom,
                )

            if (response.isSuccessful) {
                response.body() ?: throw RuntimeException("이상증상 수정 API 통신 에러")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    "이상증상 수정 API 통신 에러 코드 ${response.code()}, 전문: $errorBody"
                throw RuntimeException(errorMessage)
            }
        }
}
