package com.project.meongcare.feed.model.utils

import android.content.Context
import android.net.Uri
import android.widget.TextView
import com.google.gson.Gson
import com.project.meongcare.feed.model.entities.FeedInfo
import com.project.meongcare.feed.model.entities.FeedPutInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object FeedInfoUtils {
    fun convertFeedPostDto(feedInfo: FeedInfo): RequestBody {
        val json = Gson().toJson(feedInfo)
        return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun convertFeedPutDto(feedPutInfo: FeedPutInfo): RequestBody {
        val json = Gson().toJson(feedPutInfo)
        return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun convertFeedFile(
        context: Context,
        uri: Uri,
    ): MultipartBody.Part {
        if (uri.toString().isEmpty()) {
            val emptyFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("file", "", emptyFile)
        }

        // Uri로부터 InputStream을 얻고, 임시 파일로 복사
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "tempFile")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    fun calculateRecommendDailyIntake(
        weight: Double,
        feedKcal: Double,
    ): Double {
        val dailyEnergyRequirement = 1.6 * (30 * weight + 70)
        val recommendDailyIntake = dailyEnergyRequirement * 1000 / feedKcal
        return String.format("%.2f", recommendDailyIntake).toDouble()
    }

    fun initRecommendDailyIntake(
        feedKcal: Double,
        textView: TextView,
    ) {
        val weight = 15.0
        val recommendIntake = calculateRecommendDailyIntake(weight, feedKcal)
        textView.apply {
            text = if (recommendIntake == Double.POSITIVE_INFINITY) {
                "0g"
            }  else {
                "${recommendIntake}g"
            }
        }
    }
}
