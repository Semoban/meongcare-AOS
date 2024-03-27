package com.project.meongcare.aws.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID

object ProfileImageUtils {
    private fun createUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun createMultipartFromUri(
        context: Context,
        uri: Uri?,
    ): MultipartBody.Part {
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, createUUID())
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            return MultipartBody.Part.createFormData("file", file.name, requestFile)
        }
        val emptyBody = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", "", emptyBody)
    }
}
