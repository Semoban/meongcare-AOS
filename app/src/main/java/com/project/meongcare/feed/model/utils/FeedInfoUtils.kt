package com.project.meongcare.feed.model.utils

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.project.meongcare.excreta.model.entities.ExcretaInfo
import com.project.meongcare.excreta.model.entities.ExcretaInfoPatch
import com.project.meongcare.feed.model.entities.FeedInfo
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
}
