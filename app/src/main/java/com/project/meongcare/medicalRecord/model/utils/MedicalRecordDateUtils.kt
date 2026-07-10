package com.project.meongcare.medicalRecord.model.utils

object MedicalRecordDateUtils {
    fun showFormattedTime(dateTime: String): String {
        val time = dateTime.substringAfterLast("T")
        val hourMinute = time.substringBeforeLast(":")

        val hour = hourMinute.substringBeforeLast(":").toInt()
        val minute = hourMinute.substringAfterLast(":")

        return when {
            hour == 0 -> "오전 12:$minute"
            hour < 12 -> "오전 $hour:$minute"
            hour == 12 -> "오후 12:$minute"
            else -> "오후 ${hour - 12}:$minute"
        }
    }
}
