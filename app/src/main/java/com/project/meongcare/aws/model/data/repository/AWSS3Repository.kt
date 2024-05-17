package com.project.meongcare.aws.model.data.repository

import com.project.meongcare.aws.model.entities.AWSS3Response
import okhttp3.RequestBody

interface AWSS3Repository {
    suspend fun getPreSignedUrl(
        accessToken: String,
        fileName: String,
    ): AWSS3Response?

    suspend fun uploadImageToS3(
        preSignedUrl: String,
        image: RequestBody,
    ): Int?
}
