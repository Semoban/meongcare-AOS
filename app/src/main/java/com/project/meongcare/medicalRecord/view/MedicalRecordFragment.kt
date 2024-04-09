package com.project.meongcare.medicalRecord.view

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordBinding
import com.project.meongcare.medicalRecord.model.data.local.MedicalRecordItemClickListener
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
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

    private lateinit var medicalRecordListAdapter: MedicalRecordListAdapter

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

        medicalRecordListAdapter =
            MedicalRecordListAdapter(
                object : MedicalRecordItemClickListener {
                    override fun onMedicalRecordItemClick(medicalRecordId: Long) {
                        // 상세 화면으로 이동하면서 medicalRecordId 전달
                        Log.d("medicalRecordItemClicked", "$medicalRecordId")
                    }
                },
            )

        initMedicalRecordRecyclerView()
        initCurrentDate()
        getMedicalRecordList()
        initMedicalRecordPetName()
        initCalendarView()
        initMedicalRecordListEditButton()
        initMedicalRecordListAddButton()
    }

    private fun initMedicalRecordListEditButton() {
        binding.textviewMedicalrecordEdit.setOnClickListener {
            if (medicalRecordViewModel.selectedDate.value == null) {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.medicalrecrod_empty_date),
                ).show()
            } else if (medicalRecordViewModel.medicalRecordList.value?.body()?.records?.size == 0) {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.medicalrecord_no_list),
                ).show()
            } else {
                val bundle = Bundle()
                bundle.putString("selectedDate", "${medicalRecordViewModel.selectedDate.value}")
                findNavController().navigate(R.id.action_medicalRecordFragment_to_medicalRecordEditFragment, bundle)
            }
        }
    }

    private fun initMedicalRecordListAddButton() {
        binding.textviewMedicalrecordAdd.setOnClickListener {
            // 진료기록 등록 화면으로 이동
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
                        textviewMedicalrecordDate.text = ""
                        linearLayout4.visibility = View.VISIBLE
                        recyclerviewMedicalrecordHistory.visibility = View.GONE
                        medicalRecordViewModel.getCurrentDate(null)
                    }

                    override fun onFirstDateSelected(startDate: Calendar) {
                        linearLayout4.visibility = View.GONE
                        recyclerviewMedicalrecordHistory.visibility = View.VISIBLE
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

    private fun initMedicalRecordPetName() {
        dogViewModel.dogNamePreferencesLiveData.observe(viewLifecycleOwner) { dogName ->
            if (dogName != null) {
                binding.textviewMedicalrecordPetName.text = dogName
            }
        }
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
                    if (response.body()?.records?.size == 0) {
                        binding.linearLayout4.visibility = View.VISIBLE
                        binding.recyclerviewMedicalrecordHistory.visibility = View.GONE
                    } else {
                        binding.linearLayout4.visibility = View.GONE
                        binding.recyclerviewMedicalrecordHistory.visibility = View.VISIBLE
                        medicalRecordListAdapter.submitList(response.body()?.records)
                    }
                }
            }
        }
    }

    private fun initMedicalRecordRecyclerView() {
        binding.recyclerviewMedicalrecordHistory.run {
            adapter = medicalRecordListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}
