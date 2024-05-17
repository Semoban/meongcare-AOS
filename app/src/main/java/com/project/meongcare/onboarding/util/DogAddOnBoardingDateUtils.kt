package com.project.meongcare.onboarding.util

import java.text.SimpleDateFormat

object DogAddOnBoardingDateUtils {
    fun dateFormat(str: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")

        val parsedDate = inputDateFormat.parse(str)
        return outputDateFormat.format(parsedDate)
    }
}
