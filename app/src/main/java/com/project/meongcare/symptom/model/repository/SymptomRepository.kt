package com.project.meongcare.symptom.model.repository

import android.util.Log
import com.project.meongcare.TempActivity
import com.project.meongcare.symptom.model.ResultSymptom
import com.project.meongcare.symptom.model.Symptom
import com.project.meongcare.symptom.model.api.SymptomAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SymptomRepository {
    companion object {
        fun searchByDogId(dogId: Long, dateTime:String, callback: (List<Symptom>?) -> Unit) {
            val retrofit = Retrofit.Builder()
                .baseUrl(TempActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(SymptomAPI::class.java)
            val call = api.getResultSymptom(
                "",
                dogId,
                dateTime
            )


            call.enqueue(object : Callback<ResultSymptom> {
                override fun onResponse(
                    call: Call<ResultSymptom>,
                    response: Response<ResultSymptom>
                ) {
                    val result = response.body()?.records
                    callback(result)

                }


                override fun onFailure(call: Call<ResultSymptom>, t: Throwable) {
                    Log.w("Symptom API", "통신 실패: ${t.message}")
                    callback(null)
                }
            })

        }

    }
}