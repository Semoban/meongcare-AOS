package com.project.meongcare.medicalRecord.model.utils

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MedicalRecordUtils {
    companion object {
        fun convertMDateToSimpleDate(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertMDateToSimpleTime(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertMedicalRecordDto(medicalRecordDto: MedicalRecordDto): RequestBody {
            val json = Gson().toJson(medicalRecordDto)
            return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        }

        fun convertPictureToFile(
            context: Context,
            uri: Uri,
        ): MultipartBody.Part {
            if (uri.toString().isEmpty()) {
                val emptyFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                return MultipartBody.Part.createFormData("file", "", emptyFile)
            }

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

        fun hideKeyboard(view: View) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
