package com.project.meongcare.medicalrecord.model.utils

object MedicalRecordDateUtils {
    fun showFormattedTime(dateTime: String): String {
        val time = dateTime.substringAfterLast("T")
        val hourMinute = time.substringBeforeLast(":")

        val hour = hourMinute.substringBeforeLast(":")
        val minute = hourMinute.substringAfterLast(":")

        return if (hour.toInt() < 12) {
            "오전 $hour:$minute"
        } else {
            "오후 $hour:$minute"
        }
    }
}
