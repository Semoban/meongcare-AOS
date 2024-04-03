package com.project.meongcare.aws.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID

object AWSS3ImageUtils {
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

    fun convertUriToFile(
        context: Context,
        uri: Uri,
    ): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, createUUID())
        file.deleteOnExit()

        contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }

        return file
    }

    suspend fun createMultipartFromUrl(
        context: Context,
        url: String?
    ): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            if (url.isNullOrEmpty()) {
                val emptyBody = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", "", emptyBody)
            } else {
                val inputStream = URL(url).openStream()
                val file = File(context.cacheDir, createUUID())
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            }
        }
    }

    fun getMultipartFileName(file: MultipartBody.Part): String {
        val disposition = file.headers?.get("Content-Disposition")
        return disposition?.substringAfterLast("filename=")?.removeSurrounding("\"") ?: "tempFile"
    }
}
