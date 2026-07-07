package com.project.meongcare.notice.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object NoticeDateTimeUtils {
    private const val HOURS_OF_DAY = 24L

    fun formatLastUpdateTime(lastUpdateTime: String): String {
        val dateTime = parseLocalDateTime(lastUpdateTime)
        val hoursDiff = getHoursDifference(dateTime)
        val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("a hh:mm"))

        return when {
            hoursDiff < HOURS_OF_DAY -> "오늘 $formattedTime"
            hoursDiff < HOURS_OF_DAY * 2 -> "어제 $formattedTime"
            dateTime.year == LocalDateTime.now().year -> dateTime.format(DateTimeFormatter.ofPattern("MM월 dd일"))
            else -> dateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        }
    }

    fun isNewNotice(lastUpdateTime: String): Boolean = getHoursDifference(parseLocalDateTime(lastUpdateTime)) < HOURS_OF_DAY

    private fun parseLocalDateTime(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    private fun getHoursDifference(dateTime: LocalDateTime): Long = ChronoUnit.HOURS.between(dateTime, LocalDateTime.now())
}
