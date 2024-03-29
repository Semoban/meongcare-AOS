package com.project.meongcare

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val peekHeightInPixels = 0

        val behavior = dialog.behavior
        if (behavior != null) {
            behavior.peekHeight = peekHeightInPixels
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

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
            val currentMonth = Calendar.getInstance()
            val pastMonth = Calendar.getInstance()
            pastMonth.add(Calendar.MONTH, -282)
            calendarBottomSheet.setVisibleMonthRange(pastMonth, currentMonth)
            calendarBottomSheet.setCurrentMonth(currentMonth)

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
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        inputDateFormat.timeZone = TimeZone.getTimeZone("GMT")

        val parsedDate = inputDateFormat.parse(date.toString())
        return outputDateFormat.format(parsedDate)
    }
}
