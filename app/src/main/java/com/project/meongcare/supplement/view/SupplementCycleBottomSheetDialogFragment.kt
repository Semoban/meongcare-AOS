package com.project.meongcare.supplement.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.BottomsheetSupplementAddCycleBinding
import com.project.meongcare.databinding.BottomsheetSymptomAddDateBinding
import java.time.LocalDate

class SupplementCycleBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentSupplementCycleBottomSheetBinding: BottomsheetSupplementAddCycleBinding

    interface OnNumberCycleChangedListener {
        fun onNumberCycleChanged(number: Int)
    }

    var onNumberCycleChangedListener: OnNumberCycleChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementCycleBottomSheetBinding = BottomsheetSupplementAddCycleBinding.inflate(inflater, container, false)

        fragmentSupplementCycleBottomSheetBinding.run {
            var currentNumber = 0
            val addButton = imageViewBottomsheetSupplementAddCycleNumberPlus
            val minusButton = imageViewBottomsheetSupplementAddCycleNumberMinus
            val numberCycle = textViewBottomsheetSupplementAddCycleNumber

            addButton.setOnClickListener {
                currentNumber++
                numberCycle.text = currentNumber.toString()
            }

            minusButton.setOnClickListener {
                if (currentNumber > 0) {
                    currentNumber--
                    numberCycle.text = currentNumber.toString()
                }
            }
            buttonBottomsheetSupplementAddCycleComplete.setOnClickListener {
                onNumberCycleChangedListener?.onNumberCycleChanged(currentNumber)
                dismiss()
            }
        }

        return fragmentSupplementCycleBottomSheetBinding.root
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
    }
}
