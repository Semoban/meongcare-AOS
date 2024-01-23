package com.project.meongcare.feed.model.entities

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class FeedUploadRequest(
    val dto: RequestBody,
    val file: MultipartBody.Part,
)
