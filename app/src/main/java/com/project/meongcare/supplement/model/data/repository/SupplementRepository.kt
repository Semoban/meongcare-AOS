package com.project.meongcare.supplement.model.data.repository

import android.util.Log
import com.google.gson.Gson
import com.project.meongcare.MainActivity
import com.project.meongcare.supplement.model.data.remote.SupplementAPI
import com.project.meongcare.supplement.model.entities.ResponseSupplement
import com.project.meongcare.supplement.model.entities.ResultSupplement
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.model.entities.SupplementDto
import com.project.meongcare.symptom.model.entities.ResponseSymptom
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.reflect.Type

class SupplementRepository {
    companion object {
        fun searchByDogId(
            dogId: Int,
            dateTime: String,
            callback: (List<Supplement>?) -> Unit,
        ) {
            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SupplementAPI::class.java)
            val call =
                api.getResultSupplement(
                    MainActivity.ACCESS_TOKEN,
                    dogId,
                    dateTime,
                )

            call.enqueue(
                object : Callback<ResultSupplement> {
                    override fun onResponse(
                        call: Call<ResultSupplement>,
                        response: Response<ResultSupplement>,
                    ) {
                        Log.d("Supplement API response body", "통신 성공: ${response.body()}")
                        Log.d("Supplement API response", "통신 성공: ${response.body()}")
                        val result = response.body()?.routines
                        callback(result)
                    }

                    override fun onFailure(
                        call: Call<ResultSupplement>,
                        t: Throwable,
                    ) {
                        Log.w("Supplement API", "통신 실패: ${t.message}")
                        callback(null)
                    }
                },
            )
        }

        fun addSupplement(supplementDto: SupplementDto, file: File) {
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(SupplementAPI::class.java)

            val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

            val gson = Gson()
            val json = gson.toJson(supplementDto)
            val supplementRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

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


        private val nullOnEmptyConverterFactory =
            object : Converter.Factory() {
                fun converterFactory() = this

                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<out Annotation>,
                    retrofit: Retrofit,
                ) = object : Converter<ResponseBody, Any?> {
                    val nextResponseBodyConverter =
                        retrofit.nextResponseBodyConverter<Any?>(
                            converterFactory(),
                            type,
                            annotations,
                        )

                    override fun convert(value: ResponseBody) =
                        if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
                }
            }
    }
}

