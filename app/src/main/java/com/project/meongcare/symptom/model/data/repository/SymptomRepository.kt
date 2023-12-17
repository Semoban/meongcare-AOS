package com.project.meongcare.symptom.model.data.repository

import android.util.Log
import com.project.meongcare.MainActivity
import com.project.meongcare.symptom.model.data.remote.SymptomAPI
import com.project.meongcare.symptom.model.entities.ResponseSymptom
import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class SymptomRepository {
    companion object {
        fun searchByDogId(
            dogId: Int,
            dateTime: String,
            callback: (List<Symptom>?) -> Unit,
        ) {
            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call =
                api.getResultSymptom(
                    MainActivity.ACCESS_TOKEN,
                    dogId,
                    dateTime,
                )

            call.enqueue(
                object : Callback<ResultSymptom> {
                    override fun onResponse(
                        call: Call<ResultSymptom>,
                        response: Response<ResultSymptom>,
                    ) {
                        Log.d("Symptom API response body", "통신 성공: ${response.body()}")
                        Log.d("Symptom API response", "통신 성공: ${response.body()}")
                        val result = response.body()?.records
                        callback(result)
                    }

                    override fun onFailure(
                        call: Call<ResultSymptom>,
                        t: Throwable,
                    ) {
                        Log.w("Symptom API", "통신 실패: ${t.message}")
                        callback(null)
                    }
                },
            )
        }

        fun addSymptom(toAddSymptom: ToAddSymptom) {
            val retrofit =
                Retrofit.Builder().baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call =
                api.addSymptom(
                    MainActivity.ACCESS_TOKEN,
                    toAddSymptom,
                )

            call.enqueue(
                object : Callback<ResponseSymptom> {
                    override fun onResponse(
                        call: Call<ResponseSymptom>,
                        response: Response<ResponseSymptom>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("Symptom API", "통신 성공: ${response.body()}, $toAddSymptom")
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseSymptom>,
                        t: Throwable,
                    ) {
                        Log.w("Symptom API", "통신 실패: ${t.message}")
                    }
                },
            )
        }

        fun deleteSymptom(symptomIds: Array<Int>) {
            val retrofit =
                Retrofit.Builder().baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call =
                api.deleteSymptom(
                    MainActivity.ACCESS_TOKEN,
                    symptomIds,
                )

            call.enqueue(
                object : Callback<ResponseSymptom> {
                    override fun onResponse(
                        call: Call<ResponseSymptom>,
                        response: Response<ResponseSymptom>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("Symptom API", "통신 성공: ${response.body()}, $symptomIds")
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseSymptom>,
                        t: Throwable,
                    ) {
                        Log.w("Symptom API", "통신 실패: ${t.message}")
                    }
                },
            )
        }

        fun editSymptom(toEditSymptom: ToEditSymptom) {
            val retrofit =
                Retrofit.Builder().baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call =
                api.editSymptom(
                    MainActivity.ACCESS_TOKEN,
                    toEditSymptom,
                )

            call.enqueue(
                object : Callback<ResponseSymptom> {
                    override fun onResponse(
                        call: Call<ResponseSymptom>,
                        response: Response<ResponseSymptom>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("Symptom API", "통신 성공: ${response.body()}, $toEditSymptom")
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseSymptom>,
                        t: Throwable,
                    ) {
                        Log.w("Symptom API", "통신 실패: ${t.message}")
                    }
                },
            )
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

