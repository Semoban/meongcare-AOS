package com.project.meongcare.medicalrecord.view

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordBinding
import com.project.meongcare.medicalrecord.viewmodel.DogViewModel
import com.project.meongcare.medicalrecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalrecord.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class MedicalRecordFragment : Fragment() {
    private lateinit var binding: FragmentMedicalRecordBinding

    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    private var accessToken = ""
    private var dogId = 0L

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

        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
            }
        }

        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
            }
        }

        initCurrentDate()
        getMedicalRecordList()
        initCalendarView()
        initMedicalRecordListEditButton()
    }

    private fun initMedicalRecordListEditButton() {
        binding.textviewMedicalrecordEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedDate", "${medicalRecordViewModel.selectedDate.value}")
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
                        val selectedDate = dateTimeToDate(startDate.time)
                        medicalRecordViewModel.getCurrentDate(selectedDate)
                    }
                },
            )
        }
    }

    private fun dateTimeToDate(date: Date): String {
        val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        inputDateFormat.timeZone = TimeZone.getTimeZone("GMT")

        val parsedDate = inputDateFormat.parse(date.toString())
        return outputDateFormat.format(parsedDate)
    }

    private fun dateFormat(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputDateFormat = SimpleDateFormat("yyyy. MM. dd")

        val parsedDate = inputDateFormat.parse(date)
        return outputDateFormat.format(parsedDate)
    }

    private fun initMedicalRecordDateTextView(selectedDate: String) {
        val date = dateFormat(selectedDate)
        binding.textviewMedicalrecordDate.text = date
    }

    private fun initCurrentDate() {
        medicalRecordViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                initMedicalRecordDateTextView(date)
                medicalRecordViewModel.getMedicalRecordList(
                    dogId,
                    date + "T00:00:00",
                    accessToken,
                )
            }
        }
    }

    private fun getMedicalRecordList() {
        medicalRecordViewModel.medicalRecordList.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.code() == 200) {
                    Log.d("medicalRecordList", response.body()?.records?.size.toString())
                }
            }
        }
    }
}
