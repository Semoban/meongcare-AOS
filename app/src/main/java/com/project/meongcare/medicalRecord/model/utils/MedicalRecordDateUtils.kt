package com.project.meongcare.medicalRecord.model.utils

import com.project.meongcare.LocaleDateTimeFormats
import java.time.LocalTime

object MedicalRecordDateUtils {
    fun showFormattedTime(dateTime: String): String {
        val time = dateTime.substringAfterLast("T")
        val hourMinute = time.substringBeforeLast(":")

        val hour = hourMinute.substringBeforeLast(":").toInt()
        val minute = hourMinute.substringAfterLast(":").toInt()

        return LocaleDateTimeFormats.time12hShort().format(LocalTime.of(hour, minute))
    }
}
