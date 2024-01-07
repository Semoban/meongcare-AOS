package com.project.meongcare.feed.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedAddEditBinding
import com.project.meongcare.feed.model.data.local.FeedPhotoListener
import com.project.meongcare.feed.viewmodel.FeedPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class FeedAddFragment : Fragment(), FeedPhotoListener {
    private var _binding: FragmentFeedAddEditBinding? = null
    private val binding get() = _binding!!

    private val feedPostViewModel: FeedPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initPhotoAttachModalBottomSheet()
        updateCalendarVisibility()
        updateSelectedIntakePeriod()
    }

    private fun initToolbar() {
        binding.toolbarFeedaddedit.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPhotoAttachModalBottomSheet() {
        binding.cardviewFeedaddeditImage.setOnClickListener {
            val photoAttachModalBottomSheet = FeedPhotoAttachModalBottomSheetFragment()
            photoAttachModalBottomSheet.setPhotoListener(this@FeedAddFragment)
            photoAttachModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                FeedPhotoAttachModalBottomSheetFragment.TAG,
            )
        }
    }

    private fun updateCalendarVisibility() {
        binding.apply{
            textviewFeedaddeditIntakePeriodStart.apply {
                setOnClickListener {
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditStartDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditEndDate.visibility = View.GONE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditIntakePeriodEnd.setTextColor(resources.getColor(R.color.gray4, null))
                }
            }
            textviewFeedaddeditIntakePeriodEnd.apply {
                setOnClickListener {
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditEndDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditStartDate.visibility = View.INVISIBLE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditIntakePeriodStart.setTextColor(resources.getColor(R.color.gray4, null))
                }
            }
        }
    }

    private fun updateSelectedIntakePeriodStartDate(
        calendar: DateRangeCalendarView,
        date: TextView,
    ) {
        calendar.setCalendarListener(object : CalendarListener {
            override fun onDateRangeSelected(
                startDate: Calendar,
                endDate: Calendar,
            ) {
                calendar.resetAllSelectedViews()
            }

            override fun onFirstDateSelected(startDate: Calendar) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDate = dateFormat.format(startDate.time)

                date.text = convertDateFormat(selectedDate)
            }
        })
    }

    private fun updateSelectedIntakePeriodEndDate(
        calendar: DateRangeCalendarView,
        date: TextView,
        checkBox: CheckBox,
    ) {
        calendar.setCalendarListener(object : CalendarListener {
            override fun onDateRangeSelected(
                startDate: Calendar,
                endDate: Calendar,
            ) {
                calendar.resetAllSelectedViews()
            }

            override fun onFirstDateSelected(startDate: Calendar) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDate = dateFormat.format(startDate.time)
                date.text = convertDateFormat(selectedDate)

                checkBox.setOnClickListener {
                    calendar.resetAllSelectedViews()
                    date.text = "모름"
                }

                if (date.text != "모름") {
                    checkBox.isChecked = false
                }
            }
        })
    }

    private fun updateSelectedIntakePeriod() {
        binding.apply {
            updateSelectedIntakePeriodStartDate(
                calendarviewFeedaddeditStartDate,
                textviewFeedaddeditIntakePeriodStart
            )
            updateSelectedIntakePeriodEndDate(
                calendarviewFeedaddeditEndDate,
                textviewFeedaddeditIntakePeriodEnd,
                checkboxFeedaddeditDoNotKnowEndDate
            )
        }
    }

    override fun onUriPassed(uri: Uri) {
        feedPostViewModel.getFeedImage(uri)
        binding.apply {
            Glide.with(this@FeedAddFragment)
                .load(uri)
                .into(imageviewFeedaddeditPicture)
            layoutFeedaddeditImage.root.visibility = View.INVISIBLE
        }
    }

    fun convertDateFormat(date: String): String {
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(date)
        return outputFormat.format(parsedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
