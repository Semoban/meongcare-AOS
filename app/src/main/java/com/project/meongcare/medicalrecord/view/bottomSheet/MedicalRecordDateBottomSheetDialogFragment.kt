package com.project.meongcare.medicalRecord.view.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.BottomsheetMedicalRecordAddDateBinding
import java.time.LocalDate

class MedicalRecordDateBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentMedicalRecordBottomSheetBinding: BottomsheetMedicalRecordAddDateBinding

    interface OnDateSelectedListener {
        fun onDateSelected(date: LocalDate)
    }

    var onDateSelectedListener: OnDateSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentMedicalRecordBottomSheetBinding = BottomsheetMedicalRecordAddDateBinding.inflate(inflater, container, false)

        fragmentMedicalRecordBottomSheetBinding.buttonBottomsheetMedicalRecordAddDateComplete.setOnClickListener {
            val datePicker = fragmentMedicalRecordBottomSheetBinding.datepickerBottomsheetMedicalRecordAddDate
            val selectedDate =
                LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            onDateSelectedListener?.onDateSelected(selectedDate)
            dismiss()
        }

        return fragmentMedicalRecordBottomSheetBinding.root
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
        fragmentMedicalRecordBottomSheetBinding.run {
            val datePickerHeaderId =
                fragmentMedicalRecordBottomSheetBinding.datepickerBottomsheetMedicalRecordAddDate.getChildAt(0)
                    .resources.getIdentifier("date_picker_header", "id", "android")
            fragmentMedicalRecordBottomSheetBinding.root.findViewById<View>(datePickerHeaderId).visibility =
                View.GONE
        }
    }

    fun setOnDateSelecetedListener(listener: OnDateSelectedListener) {
        this.onDateSelectedListener = listener
    }
}
