package com.project.meongcare.medicalRecord.utils

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MedicalRecordUtils {
    companion object {
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

        fun convertMedicalRecordDto(medicalRecordDto: MedicalRecordDto): RequestBody {
            val json = Gson().toJson(medicalRecordDto)
            return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        }
    }
}