package com.project.meongcare.onboarding.util

import com.project.meongcare.LocaleDateTimeFormats
import java.time.LocalDate

object DogAddOnBoardingDateUtils {
    fun dateFormat(str: String): String {
        val parsedDate = LocalDate.parse(str)
        return LocaleDateTimeFormats.datePadded().format(parsedDate)
    }
}
