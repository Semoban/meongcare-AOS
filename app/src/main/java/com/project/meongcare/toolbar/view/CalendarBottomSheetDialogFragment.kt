package com.project.meongcare.toolbar.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.BottomsheetCalendarBinding
import java.time.LocalDate

class CalendarBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentCalendarBottomSheetBinding: BottomsheetCalendarBinding

    interface OnDateSelectedListener {
        fun onDateSelected(date: LocalDate)
    }

    var onDateSelectedListener: OnDateSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentCalendarBottomSheetBinding = BottomsheetCalendarBinding.inflate(inflater, container, false)

        fragmentCalendarBottomSheetBinding.buttonBottomsheetCalendarComplete.setOnClickListener {
            val datePicker = fragmentCalendarBottomSheetBinding.datepickerBottomsheetCalendar
            val selectedDate =
                LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            onDateSelectedListener?.onDateSelected(selectedDate)
            dismiss()
        }

        return fragmentCalendarBottomSheetBinding.root
    }

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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        removeDatePickerHeader()
    }

    fun removeDatePickerHeader() {
        fragmentCalendarBottomSheetBinding.run {
            val datePickerHeaderId =
                fragmentCalendarBottomSheetBinding.datepickerBottomsheetCalendar.getChildAt(0)
                    .resources.getIdentifier("date_picker_header", "id", "android")
            fragmentCalendarBottomSheetBinding.root.findViewById<View>(datePickerHeaderId).visibility =
                View.GONE
        }
    }
}
