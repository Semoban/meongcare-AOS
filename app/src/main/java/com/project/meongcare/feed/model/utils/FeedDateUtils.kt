package com.project.meongcare.feed.model.utils

import com.project.meongcare.LocaleDateTimeFormats
import java.time.LocalDate

object FeedDateUtils {
    fun convertDateFormat(date: String?): String {
        val parsedDate = LocalDate.parse(date)

        return LocaleDateTimeFormats.datePadded().format(parsedDate)
    }
}
