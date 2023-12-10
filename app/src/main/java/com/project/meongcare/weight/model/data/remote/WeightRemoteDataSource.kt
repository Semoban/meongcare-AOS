package com.project.meongcare.weight.model.data.remote

import android.util.Log
import com.project.meongcare.weight.model.entities.WeightPostRequest
import org.json.JSONObject

class WeightRemoteDataSource {
    suspend fun postWeight(
        weightPostRequest: WeightPostRequest,
    ): Int? {
        try {
            val response = WeightClient.weightService.postWeight(
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NiwiZXhwIjoxNzAxODg2MjY3fQ.d8YtnaopY7HogGe82ExXTZ87TT7b344ZqY21Z0T49hg",
                weightPostRequest,
            )

            return if (response.code() == 200) {
                Log.d("WeightSuccess", response.code().toString())
                response.body()
            } else {
                Log.d("WeightFailure", response.code().toString())
                val stringToJson = JSONObject(response.errorBody()?.string()!!)
                Log.d("WeightFailure", "$stringToJson")
                null
            }
        } catch (e: Exception) {
            Log.e("WeightException", e.toString())
            return null
        }
    }
}
