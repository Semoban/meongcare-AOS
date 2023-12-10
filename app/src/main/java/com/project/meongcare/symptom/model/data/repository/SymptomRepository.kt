package com.project.meongcare.symptom.model.data.repository

import android.util.Log
import com.project.meongcare.MainActivity
import com.project.meongcare.symptom.model.data.remote.SymptomAPI
import com.project.meongcare.symptom.model.entities.ResponseSymptom
import com.project.meongcare.symptom.model.entities.ResultSymptom
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class SymptomRepository {
    companion object {
        fun searchByDogId(
            dogId: Int,
            dateTime: LocalDateTime,
            callback: (List<Symptom>?) -> Unit,
        ) {
            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call =
                api.getResultSymptom(
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZXhwIjoxNzAxMjY5MDA3fQ.Zcccin4mVzpP2vvwTe84F5vFKlPzP85w3F5nCvMvT84",
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
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call =
                api.addSymptom(
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZXhwIjoxNzAxMjY5MDA3fQ.Zcccin4mVzpP2vvwTe84F5vFKlPzP85w3F5nCvMvT84",
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
    }
}

