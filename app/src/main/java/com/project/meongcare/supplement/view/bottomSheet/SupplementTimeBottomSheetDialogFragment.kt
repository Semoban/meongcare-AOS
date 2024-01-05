package com.project.meongcare.supplement.view.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.BottomsheetSupplementAddTimeBinding

class SupplementTimeBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentSupplementTimeBottomSheetBinding: BottomsheetSupplementAddTimeBinding

    interface OnNumberTimeChangedListener {
        fun onNumberTimeChanged(number: Int,time: String)
    }

    var onNumberTimeChangedListener: OnNumberTimeChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementTimeBottomSheetBinding = BottomsheetSupplementAddTimeBinding.inflate(inflater, container, false)

        fragmentSupplementTimeBottomSheetBinding.run {
            var currentNumber = 1
            val addButton = imageViewBottomsheetSupplementAddTimeNumberPlus
            val minusButton = imageViewBottomsheetSupplementAddTimeNumberMinus
            val numberAmount = textViewBottomsheetSupplementAddTimeNumber
            val timePicker = timepickerBottomsheetSupplementAddTime

            addButton.setOnClickListener {
                currentNumber++
                numberAmount.text = currentNumber.toString()
            }

            minusButton.setOnClickListener {
                if (currentNumber > 1) {
                    currentNumber--
                    numberAmount.text = currentNumber.toString()
                }
            }

            buttonBottomsheetSupplementAddTimeComplete.setOnClickListener {
                val hour = String.format("%02d", timePicker.hour)
                val minute = String.format("%02d", timePicker.minute)
                val timeValue = "$hour:$minute:00"
                Log.d("타임피커",timeValue)
                onNumberTimeChangedListener?.onNumberTimeChanged(currentNumber, timeValue)
                dismiss()
            }
        }

        return fragmentSupplementTimeBottomSheetBinding.root
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
}
