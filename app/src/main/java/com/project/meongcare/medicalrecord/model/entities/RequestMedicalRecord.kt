package com.project.meongcare.medicalRecord.model.entities

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class RequestMedicalRecord(
    val dto: RequestBody,
    val file: MultipartBody.Part,

    )