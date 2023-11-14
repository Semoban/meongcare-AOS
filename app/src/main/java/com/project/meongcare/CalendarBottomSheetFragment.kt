package com.project.meongcare

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentCalendarBottomSheetBinding

class CalendarBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var fragmentCalendarBottomSheetBinding : FragmentCalendarBottomSheetBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentCalendarBottomSheetBinding = FragmentCalendarBottomSheetBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentCalendarBottomSheetBinding.run {
            val calendarTypeface = Typeface.createFromAsset(mainActivity.assets, "pretendard_regular.otf")
            calendarCalendarBottomSheet.setFonts(calendarTypeface)
        }

        return fragmentCalendarBottomSheetBinding.root
    }
}
