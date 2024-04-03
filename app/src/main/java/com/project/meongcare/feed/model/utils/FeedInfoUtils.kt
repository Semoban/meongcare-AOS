package com.project.meongcare.feed.model.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.project.meongcare.R
import com.project.meongcare.feed.model.entities.FeedPutInfo
import com.project.meongcare.snackbar.view.CustomSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedInputStream
import java.io.File
import java.net.URL

object FeedInfoUtils {
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

    suspend fun convertFeedImageUrl(
        context: Context,
        urlString: String,
    ): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection()
                val inputStream = BufferedInputStream(connection.getInputStream())
                val file = File(context.cacheDir, "downloadedFile")
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            } catch (e: Exception) {
                Log.d("사료 이미지 URL 변환 실패", e.message.toString())

                val emptyFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", "", emptyFile)
            }
        }
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
        recommendIntake: Double,
        textView: TextView,
    ) {
        textView.apply {
            text =
                if (recommendIntake == Double.POSITIVE_INFINITY) {
                    "0"
                } else {
                    "$recommendIntake"
                }
        }
    }

    fun showSuccessSnackBar(
        view: View,
        message: String,
    ) {
        CustomSnackBar.make(
            view,
            R.drawable.snackbar_success_16dp,
            message,
        ).show()
    }

    fun showFailureSnackBar(
        view: View,
        message: String,
    ) {
        CustomSnackBar.make(
            view,
            R.drawable.snackbar_error_16dp,
            message,
        ).show()
    }
}
