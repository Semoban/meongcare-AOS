package com.project.meongcare.symptom.model.data.repository

import com.project.meongcare.MainActivity
import com.project.meongcare.symptom.model.data.remote.SymptomAPI
import com.project.meongcare.symptom.model.data.remote.SymptomRetrofitInstance
import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import okhttp3.ResponseBody

class SymptomRepository {

    companion object {
        private val symptomAPI: SymptomAPI =
            SymptomRetrofitInstance.getInstance().create(SymptomAPI::class.java)
    }


    suspend fun getSymptomByDogId(
        dogId: Int,
        dateTime: String,
    ): Result<ResultSymptom> =
        kotlin.runCatching {
            val response =
                symptomAPI.getSymptomList(
                    MainActivity.ACCESS_TOKEN,
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

    suspend fun addSymptom(toAddSymptom: ToAddSymptom): Int {
        val response =
            symptomAPI.addSymptom(
                MainActivity.ACCESS_TOKEN,
                toAddSymptom,
            )
        return response.code()
    }

    suspend fun deleteSymptom(symptomIds: IntArray): Int {
        val response =
            symptomAPI.deleteSymptom(
                MainActivity.ACCESS_TOKEN,
                symptomIds,
            )

        return response.code()
    }

    suspend fun patchSymptom(toEditSymptom: ToEditSymptom): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                symptomAPI.patchSymptom(
                    MainActivity.ACCESS_TOKEN,
                    toEditSymptom
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


