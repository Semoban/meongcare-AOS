package com.project.meongcare.supplement.model.data.repository

import android.util.Log
import com.google.gson.Gson
import com.project.meongcare.MainActivity
import com.project.meongcare.supplement.model.data.remote.RetrofitInstance
import com.project.meongcare.supplement.model.data.remote.SupplementAPI
import com.project.meongcare.supplement.model.entities.InfoSupplement
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

    suspend fun getSupplementInfos(
        dogId: Int,
    ): Result<InfoSupplement> = kotlin.runCatching {
        val response = supplementAPI.getSupplementInfos(MainActivity.ACCESS_TOKEN, dogId)
        if (response.isSuccessful) response.body()
            ?: throw RuntimeException("Supplement get info API 통신 실패")
        else throw RuntimeException("Supplement get info API 통신 실패")
    }

    suspend fun checkSupplement(supplementsRecordId: Int): Result<ResponseBody> = kotlin.runCatching {
        val response = supplementAPI.checkSupplement(MainActivity.ACCESS_TOKEN, supplementsRecordId)

        if (response.isSuccessful) response.body()
            ?: throw RuntimeException("Supplement check API 통신 실패")
        else throw RuntimeException("Supplement check API 통신 실패")
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



