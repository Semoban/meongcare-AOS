package com.project.meongcare.excreta.utils

import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.FragmentActivity
import com.project.meongcare.CalendarBottomSheetFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ExcretaDateTimeUtils {
    fun convertDateTimeFormat(dateTime: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(dateTime, inputFormat)
        return outputFormat.format(parsedDate)
    }

    fun convertDateFormat(date: String): String {
        val inputFormat = plusDay(date)
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(inputFormat)
        return outputFormat.format(parsedDate)
    }

    fun plusDay(date: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return LocalDate.parse(date, inputFormat).toString()
    }

    fun convertTimeFormat(timePiker: TimePicker): String {
        val hour = timePiker.hour
        val minute = timePiker.minute

        return String.format("%02d:%02d:00", hour, minute)
    }

    fun convertToTimeFormat(date: String): String {
        val hour = date.substring(HOUR_START, HOUR_END).toInt()
        val minute = date.substring(MINUTE_START, MINUTE_END).toInt()

        if (hour == NOON) return String.format("$AFTERNOON $TIME_FORM", hour, minute)
        if (hour > NOON) return String.format("$AFTERNOON $TIME_FORM", hour - NOON, minute)
        return String.format("$MORNING $TIME_FORM", hour, minute)
    }

    fun initCalendarModalBottomSheet(
        date: TextView,
        calendarBottomSheet: CalendarBottomSheetFragment,
        activity: FragmentActivity,
        dateError: TextView,
    ) {
        date.setOnClickListener {
            calendarBottomSheet.show(
                activity.supportFragmentManager,
                calendarBottomSheet.tag,
            )
        }
        dateError.setOnClickListener {
            it.visibility = View.GONE
            calendarBottomSheet.show(
                activity.supportFragmentManager,
                calendarBottomSheet.tag,
            )
        }
    }
}
