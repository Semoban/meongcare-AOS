package com.project.meongcare.excreta.model.entities

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ExcretaPostRequest(
    val dto: RequestBody,
    val file: MultipartBody.Part,
)
