package com.project.meongcare.symptom.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.BottomsheetSymptomAddDateBinding
import java.time.LocalDate

class SymptomBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentSymptomBottomSheetBinding: BottomsheetSymptomAddDateBinding

    interface OnDateSelectedListener {
        fun onDateSelected(date: LocalDate)
    }

    var onDateSelectedListener: OnDateSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomBottomSheetBinding = BottomsheetSymptomAddDateBinding.inflate(inflater, container, false)

        fragmentSymptomBottomSheetBinding.buttonBottomsheetSymptomAddDateComplete.setOnClickListener {
            val datePicker = fragmentSymptomBottomSheetBinding.datepickerBottomsheetSymptomAddDate
            val selectedDate =
                LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            onDateSelectedListener?.onDateSelected(selectedDate)
            dismiss()
        }

        return fragmentSymptomBottomSheetBinding.root
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
        fragmentSymptomBottomSheetBinding.run {
            val datePickerHeaderId =
                fragmentSymptomBottomSheetBinding.datepickerBottomsheetSymptomAddDate.getChildAt(0)
                    .resources.getIdentifier("date_picker_header", "id", "android")
            fragmentSymptomBottomSheetBinding.root.findViewById<View>(datePickerHeaderId).visibility =
                View.GONE
        }
    }

    fun setOnDateSelecetedListener(listener: OnDateSelectedListener) {
        this.onDateSelectedListener= listener
    }
}
