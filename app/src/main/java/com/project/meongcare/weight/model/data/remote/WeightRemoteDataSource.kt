package com.project.meongcare.weight.model.data.remote

import android.util.Log
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import org.json.JSONObject
import javax.inject.Inject

class WeightRemoteDataSource @Inject constructor(){
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

    suspend fun patchWeight(
        weightPatchRequest: WeightPatchRequest
    ): Int? {
        try {
            val response = WeightClient.weightService.patchWeight(
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NiwiZXhwIjoxNzAxODg2MjY3fQ.d8YtnaopY7HogGe82ExXTZ87TT7b344ZqY21Z0T49hg",
                weightPatchRequest.kg,
                weightPatchRequest.date,
            )

            return if (response.code() == 200) {
                Log.d("WeightPatchSuccess", response.code().toString())
                response.body()
            } else {
                val stringToJson = JSONObject(response.errorBody()?.string()!!)
                Log.d("WeightPatchFailure", response.code().toString())
                Log.d("WeightPatchFailure", "$stringToJson")
                null
            }
        } catch (e: Exception) {
            Log.e("WeightPatchException", e.toString())
            return null
        }
    }
}
