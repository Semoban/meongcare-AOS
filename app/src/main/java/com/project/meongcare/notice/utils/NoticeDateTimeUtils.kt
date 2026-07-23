package com.project.meongcare.notice.utils

import android.content.Context
import com.project.meongcare.LocaleDateTimeFormats
import com.project.meongcare.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object NoticeDateTimeUtils {
    private const val HOURS_OF_DAY = 24L

    fun formatLastUpdateTime(
        context: Context,
        lastUpdateTime: String,
    ): String {
        val dateTime = parseLocalDateTime(lastUpdateTime)
        val hoursDiff = getHoursDifference(dateTime)
        val formattedTime = dateTime.format(LocaleDateTimeFormats.time12h())

        return when {
            hoursDiff < HOURS_OF_DAY -> context.getString(R.string.notice_today_format, formattedTime)
            hoursDiff < HOURS_OF_DAY * 2 -> context.getString(R.string.notice_yesterday_format, formattedTime)
            dateTime.year == LocalDateTime.now().year -> dateTime.format(LocaleDateTimeFormats.monthDay())
            else -> dateTime.format(LocaleDateTimeFormats.datePadded())
        }
    }

    fun isNewNotice(lastUpdateTime: String): Boolean = getHoursDifference(parseLocalDateTime(lastUpdateTime)) < HOURS_OF_DAY

    private fun parseLocalDateTime(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    private fun getHoursDifference(dateTime: LocalDateTime): Long = ChronoUnit.HOURS.between(dateTime, LocalDateTime.now())
}
