package com.project.meongcare.excreta.utils

import com.google.gson.Gson
import com.project.meongcare.excreta.model.entities.ExcretaInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object ExcretaInfoUtils {
    fun convertExcretaDto(excretaInfo: ExcretaInfo): RequestBody {
        val json = Gson().toJson(excretaInfo)
        return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun convertExcretaFile(): MultipartBody.Part {
        val emptyFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", "", emptyFile)
    }
}
