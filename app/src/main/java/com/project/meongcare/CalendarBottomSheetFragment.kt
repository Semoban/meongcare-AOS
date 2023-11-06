package com.project.meongcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.project.meongcare.databinding.FragmentCalendarBottomSheetBinding

class CalendarBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentCalendarBottomSheetBinding = FragmentCalendarBottomSheetBinding.inflate(inflater)

        fragmentCalendarBottomSheetBinding.run {

        }

        return fragmentCalendarBottomSheetBinding.root
    }
}