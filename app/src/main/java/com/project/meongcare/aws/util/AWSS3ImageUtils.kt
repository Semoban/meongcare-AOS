package com.project.meongcare.aws.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object AWSS3ImageUtils {
    private fun createUUID(): String {
        return UUID.randomUUID().toString()
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
}
