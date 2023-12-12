package com.project.meongcare.weight.model.data.remote

import android.util.Log
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import org.json.JSONObject
import javax.inject.Inject

class WeightRemoteDataSource @Inject constructor(){
    private val accessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NiwiZXhwIjoxNzAxODg2MjY3fQ.d8YtnaopY7HogGe82ExXTZ87TT7b344ZqY21Z0T49hg"
    private val weightApiService = WeightClient.weightService
    
    suspend fun postWeight(
        weightPostRequest: WeightPostRequest,
    ): Int? {
        try {
            val postResponse = weightApiService.postWeight(
                accessToken,
                weightPostRequest,
            )

            return if (postResponse.code() == 200) {
                Log.d("WeightSuccess", postResponse.code().toString())
                postResponse.body()
            } else {
                Log.d("WeightFailure", postResponse.code().toString())
                val stringToJson = JSONObject(postResponse.errorBody()?.string()!!)
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
            val patchResponse = weightApiService.patchWeight(
                accessToken,
                weightPatchRequest.kg,
                weightPatchRequest.date,
            )

            return if (patchResponse.code() == 200) {
                Log.d("WeightPatchSuccess", patchResponse.code().toString())
                patchResponse.body()
            } else {
                val stringToJson = JSONObject(patchResponse.errorBody()?.string()!!)
                Log.d("WeightPatchFailure", patchResponse.code().toString())
                Log.d("WeightPatchFailure", "$stringToJson")
                null
            }
        } catch (e: Exception) {
            Log.e("WeightPatchException", e.toString())
            return null
        }
    }
}
