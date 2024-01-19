package com.project.meongcare.excreta.utils

import android.widget.TimePicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ExcretaDateTimeUtils {
    fun convertDateTimeFormat(dateTime: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(dateTime, inputFormat)
        return outputFormat.format(parsedDate)
    }

    fun convertDateFormat(date: String): String {
        val inputFormat = plusDay(date)
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(inputFormat)
        return outputFormat.format(parsedDate)
    }

    fun plusDay(date: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return LocalDate.parse(date, inputFormat).toString()
    }

    fun convertTimeFormat(timePiker: TimePicker): String {
        val hour = timePiker.hour
        val minute = timePiker.minute

        return String.format("%02d:%02d:00", hour, minute)
    }
}
