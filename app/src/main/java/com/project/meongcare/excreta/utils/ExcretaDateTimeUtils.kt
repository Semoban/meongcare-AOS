package com.project.meongcare.excreta.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ExcretaDateTimeUtils {
    fun dateTimeFormat(dateTime: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(dateTime, inputFormat)
        return outputFormat.format(parsedDate)
    }

    fun dateFormat(date: String): String {
        val inputFormat = plusDay(date)
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(inputFormat)
        return outputFormat.format(parsedDate)
    }

    fun plusDay(date: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return LocalDate.parse(date, inputFormat).plusDays(1).toString()
    }
}
