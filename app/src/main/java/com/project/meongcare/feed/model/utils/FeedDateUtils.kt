package com.project.meongcare.feed.model.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object FeedDateUtils {
    fun convertDateFormat(date: String?): String {
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        val parsedDate = LocalDate.parse(date)

        return outputFormat.format(parsedDate)
    }
}
