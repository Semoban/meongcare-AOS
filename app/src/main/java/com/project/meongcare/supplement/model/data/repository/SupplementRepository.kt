package com.project.meongcare.supplement.model.data.repository

import android.util.Log
import com.google.gson.Gson
import com.project.meongcare.MainActivity
import com.project.meongcare.supplement.model.data.remote.RetrofitInstance
import com.project.meongcare.supplement.model.data.remote.SupplementAPI
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.DogSupplement
import com.project.meongcare.supplement.model.entities.ResultSupplement
import com.project.meongcare.supplement.model.entities.SupplementDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class SupplementRepository {

    companion object {
        private val supplementAPI: SupplementAPI =
            RetrofitInstance.getInstance().create(SupplementAPI::class.java)
    }

    suspend fun getSupplements(
        dogId: Int,
        date: String,
    ): Result<ResultSupplement> = kotlin.runCatching {
        val response = supplementAPI.getResultSupplement(MainActivity.ACCESS_TOKEN, date, dogId)
        if (response.isSuccessful) response.body()
            ?: throw RuntimeException("Supplement get API 통신 실패")
        else throw RuntimeException("Supplement get API 통신 실패")
    }

    suspend fun getSupplementDetail(
        supplementsId: Int,
    ): Result<DetailSupplement> = kotlin.runCatching {
        val response = supplementAPI.getSupplementDetail(MainActivity.ACCESS_TOKEN, supplementsId)
        if (response.isSuccessful) response.body()
            ?: throw RuntimeException("영양제 상세조회 API 통신 실패")
        else throw RuntimeException("영양제 상세조회 API 통신 실패")
    }
    suspend fun getSupplementDogs(
        dogId: Int,
    ): Result<DogSupplement> = kotlin.runCatching {
        val response = supplementAPI.getSupplementDogs(MainActivity.ACCESS_TOKEN, dogId)
        if (response.isSuccessful) response.body()
            ?: throw RuntimeException("Supplement get dog API 통신 실패")
        else throw RuntimeException("Supplement get dog API 통신 실패")
    }

    suspend fun checkSupplement(supplementsRecordId: Int): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.checkSupplement(MainActivity.ACCESS_TOKEN, supplementsRecordId)

            if (response.isSuccessful) response.body()
                ?: throw RuntimeException("Supplement check API 통신 실패")
            else throw RuntimeException("Supplement check API 통신 실패")
        }

    suspend fun patchSupplementAlarm(
        supplementsId: Int,
        pushAgreement: Boolean
    ): Result<ResponseBody> = kotlin.runCatching {
        val response = supplementAPI.patchSupplementAlarmStatus(
            MainActivity.ACCESS_TOKEN,
            supplementsId,
            pushAgreement
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
        isActive: Boolean
    ): Result<ResponseBody> = kotlin.runCatching {
        val response = supplementAPI.patchSupplementActiveStatus(
            MainActivity.ACCESS_TOKEN,
            supplementsId,
            isActive
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
                supplementAPI.deleteSupplementsById(MainActivity.ACCESS_TOKEN, supplementsIds)

            if (response.isSuccessful) response.body()
                ?: throw RuntimeException("Supplement delete API 통신 실패")
            else throw RuntimeException("Supplement delete API 통신 실패")
        }

    suspend fun deleteSupplementById(supplementsId: Int): Result<ResponseBody> =
        kotlin.runCatching {
            val response =
                supplementAPI.deleteSupplementById(MainActivity.ACCESS_TOKEN, supplementsId)

            if (response.isSuccessful) response.body()
                ?: throw RuntimeException("영양제 하나 삭제 API 통신 실패")
            else throw RuntimeException("영양제 하나 삭제 API 통신 실패")
        }

    fun addSupplement(supplementDto: SupplementDto, file: File) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SupplementAPI::class.java)

        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val gson = Gson()
        val json = gson.toJson(supplementDto)
        val supplementRequestBody =
            json.toRequestBody("application/json".toMediaTypeOrNull())

        val call =
            api.addSupplement(
                MainActivity.ACCESS_TOKEN,
                filePart,
                supplementRequestBody,
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val responseString = responseBody.string()
                        Log.d("Supplement API", responseString)
                    } else {
                        Log.d("Supplement API", "Response Body is null")
                    }
                } else {
                    Log.d("Supplement API", "Response is not successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.w("Supplement API", "통신 실패: ${t.message}")
            }
        })
    }
}



