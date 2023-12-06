package com.project.meongcare

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentCalendarDontKnowVersionBottomSheetBinding

class CalendarDontKnowVersionBottomSheetFragment : Fragment() {
    lateinit var fragmentCalendarDontKnowVersionBottomSheetBinding: FragmentCalendarDontKnowVersionBottomSheetBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentCalendarDontKnowVersionBottomSheetBinding = FragmentCalendarDontKnowVersionBottomSheetBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentCalendarDontKnowVersionBottomSheetBinding.run {
            val calendarTypeface = Typeface.createFromAsset(mainActivity.assets, "pretendard_regular.otf")
            calendarCalendarBottomSheet.setFonts(calendarTypeface)
        }

        return fragmentCalendarDontKnowVersionBottomSheetBinding.root
    }
}
