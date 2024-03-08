package com.project.meongcare.medicalrecord.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordBinding
import java.util.Calendar

class MedicalRecordFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initCalendarView()
        initMedicalRecordListEditButton()
    }

    private fun initMedicalRecordListEditButton() {
        binding.textviewMedicalrecordEdit.setOnClickListener {
            // 선택된 날짜 번들 객체에 전달
            val bundle = Bundle()
            bundle.putString("selectedDate", "")
            findNavController().navigate(R.id.action_medicalRecordFragment_to_medicalRecordEditFragment, bundle)
        }
    }

    private fun initCalendarView() {
        binding.run {
            val calendarTypeface = Typeface.createFromAsset(requireContext().assets, "pretendard_regular.otf")
            calendarviewMedicalrecord.setFonts(calendarTypeface)
            val currentMonth = Calendar.getInstance()
            val pastMonth = Calendar.getInstance()
            pastMonth.add(Calendar.MONTH, -282)
            calendarviewMedicalrecord.setVisibleMonthRange(pastMonth, currentMonth)
            calendarviewMedicalrecord.setCurrentMonth(currentMonth)

            calendarviewMedicalrecord.setCalendarListener(
                object : CalendarListener {
                    override fun onDateRangeSelected(
                        startDate: Calendar,
                        endDate: Calendar,
                    ) {
                        calendarviewMedicalrecord.resetAllSelectedViews()
                    }

                    override fun onFirstDateSelected(startDate: Calendar) {
                    }
                },
            )
        }
    }
}
