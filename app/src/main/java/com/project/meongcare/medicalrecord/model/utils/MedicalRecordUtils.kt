package com.project.meongcare.medicalRecord.model.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MedicalRecordUtils {
    companion object {
        fun convertMDateToSimpleDate(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertMDateToSimpleTime(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }
    }
}