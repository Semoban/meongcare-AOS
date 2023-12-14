package com.project.meongcare

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentCalendarBottomSheetBinding
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CalendarBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var fragmentCalendarBottomSheetBinding: FragmentCalendarBottomSheetBinding
    lateinit var mainActivity: MainActivity

    private var dateSubmitListener: DateSubmitListener? = null
    private var currentDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentCalendarBottomSheetBinding = FragmentCalendarBottomSheetBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentCalendarBottomSheetBinding.run {
            val calendarTypeface = Typeface.createFromAsset(mainActivity.assets, "pretendard_regular.otf")
            calendarBottomSheet.setFonts(calendarTypeface)

            calendarBottomSheet.setCalendarListener(
                object : CalendarListener {
                    override fun onDateRangeSelected(
                        startDate: Calendar,
                        endDate: Calendar,
                    ) {
                        calendarBottomSheet.resetAllSelectedViews()
                    }

                    override fun onFirstDateSelected(startDate: Calendar) {
                        currentDate = dateFormat(startDate.time)
                    }
                },
            )

            buttonSubmit.setOnClickListener {
                currentDate?.let { sendDate(it) }
                dismiss()
            }
        }

        return fragmentCalendarBottomSheetBinding.root
    }

    private fun sendDate(str: String) {
        dateSubmitListener?.onDateSubmit(str)
    }

    fun setDateSubmitListener(listener: DateSubmitListener) {
        this.dateSubmitListener = listener
    }

    fun dateFormat(date: Date): String {
        val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd")

        inputDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        outputDateFormat.timeZone = TimeZone.getTimeZone("GMT")

        val parsedDate = inputDateFormat.parse(date.toString())
        return outputDateFormat.format(parsedDate)
    }
}
