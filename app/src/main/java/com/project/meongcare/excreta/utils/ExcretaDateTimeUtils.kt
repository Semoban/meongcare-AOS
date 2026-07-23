package com.project.meongcare.excreta.utils

import com.project.meongcare.LocaleDateTimeFormats
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ExcretaDateTimeUtils {
    fun convertDateTimeFormat(dateTime: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val parsedDate = LocalDate.parse(dateTime, inputFormat)
        return LocaleDateTimeFormats.datePadded().format(parsedDate)
    }

    fun convertDateFormat(date: String): String {
        val inputFormat = plusDay(date)

        val parsedDate = LocalDate.parse(inputFormat)
        return LocaleDateTimeFormats.datePadded().format(parsedDate)
    }

    fun plusDay(date: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return LocalDate.parse(date, inputFormat).toString()
    }

    fun convertTimeFormat(
        hour: Int,
        minute: Int,
    ): String {
        return String.format("%02d:%02d:00", hour, minute)
    }

    fun convertToTimeFormat(date: String): String {
        val hour = date.substring(HOUR_START, HOUR_END).toInt()
        val minute = date.substring(MINUTE_START, MINUTE_END).toInt()

        return LocaleDateTimeFormats.time12h().format(LocalTime.of(hour, minute))
    }
}
