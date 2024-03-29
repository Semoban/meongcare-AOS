package com.project.meongcare.home.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

object HomeDateUtil {
    fun dateFormatter(date: Date): String {
        val currentDate = getCurrentDate()
        val selectedDate = dateToString(date)
        if (selectedDate == currentDate) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return LocalDate.now().atStartOfDay().format(formatter)
        } else {
            return (selectedDate + "T23:59:59")
        }
    }

    fun dateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }

    fun stringToDate(str: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.parse(str)
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}
