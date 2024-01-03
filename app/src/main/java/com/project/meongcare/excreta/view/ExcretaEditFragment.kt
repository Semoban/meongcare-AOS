package com.project.meongcare.excreta.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaAddEditBinding
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateTimeFormat
import com.project.meongcare.excreta.utils.HOUR_END
import com.project.meongcare.excreta.utils.HOUR_START
import com.project.meongcare.excreta.utils.MINUTE_END
import com.project.meongcare.excreta.utils.MINUTE_START
import com.project.meongcare.excreta.viewmodel.ExcretaAddViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExcretaEditFragment : Fragment(), DateSubmitListener {
    private var _binding: FragmentExcretaAddEditBinding? = null
    private val binding get() = _binding!!

    private val excretaAddViewModel: ExcretaAddViewModel by viewModels()
    private lateinit var excretaInfo: ExcretaDetailGetResponse
    private var excretaDate = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        excretaInfo = getExcretaInfo()
        initToolbar()
        initDate()
        initExcretaImage()
        initExcretaCheckBox(excretaInfo.excretaType)
        initTime()
        initCalendarModalBottomSheet()
        toggleExcretaCheckboxesOnClick()
        observeAndUpdateExcretaDate()
    }

    private fun initToolbar() {
        binding.toolbarExcretaadd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initDate() {
        binding.textviewExcretaaddDate.apply {
            setTextColor(resources.getColor(R.color.black, null))
            setTextAppearance(R.style.Typography_Body1_Medium)
            text = convertDateTimeFormat(excretaInfo.dateTime)
        }
    }

    private fun initExcretaImage() {
        val excretaImageURL = excretaInfo.excretaImageURL
        binding.apply {
            if (excretaImageURL.isNotEmpty()) {
                Glide.with(this@ExcretaEditFragment)
                    .load(excretaImageURL)
                    .into(imageviewExcretaaddPicture)
                includeExcretaAddEditAttachPhoto.root.visibility = View.INVISIBLE
            }
        }
    }

    private fun initExcretaCheckBox(excretaType: String) {
        binding.apply {
            if (excretaType == Excreta.FECES.toString()) {
                checkboxExcretaaddFeces.isChecked = true
                checkboxExcretaaddUrine.isChecked = false
            } else {
                checkboxExcretaaddUrine.isChecked = true
                checkboxExcretaaddFeces.isChecked = false
            }
        }
    }

    private fun initTime() {
        binding.timepikerExcretaaddTime.apply {
            val dateTime = excretaInfo.dateTime
            hour = dateTime.substring(HOUR_START, HOUR_END).toInt()
            minute = dateTime.substring(MINUTE_START, MINUTE_END).toInt()
        }
    }

    private fun initCalendarModalBottomSheet() {
        binding.textviewExcretaaddDate.setOnClickListener {
            val calendarModalBottomSheet = CalendarBottomSheetFragment()
            calendarModalBottomSheet.setDateSubmitListener(this@ExcretaEditFragment)
            calendarModalBottomSheet.show(
                requireActivity().supportFragmentManager, calendarModalBottomSheet.tag
            )
        }
    }

    private fun toggleExcretaCheckboxesOnClick() {
        binding.run {
            checkboxExcretaaddFeces.setOnClickListener {
                checkboxExcretaaddUrine.isChecked = !checkboxExcretaaddFeces.isChecked
            }
            checkboxExcretaaddUrine.setOnClickListener {
                checkboxExcretaaddFeces.isChecked = !checkboxExcretaaddUrine.isChecked
            }
        }
    }

    private fun observeAndUpdateExcretaDate(): String {
        excretaAddViewModel.excretaDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                excretaDate = ExcretaDateTimeUtils.plusDay(date)
                binding.textviewExcretaaddDate.run {
                    setTextColor(resources.getColor(R.color.black, null))
                    setTextAppearance(R.style.Typography_Body1_Medium)
                    text = ExcretaDateTimeUtils.convertDateFormat(date)
                }
            }
        }
        return excretaDate
    }

    private fun getExcretaInfo() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable("excretaInfo", ExcretaDetailGetResponse::class.java)!!
    } else {
        arguments?.getParcelable("excretaInfo")!!
    }

    override fun onDateSubmit(str: String) {
        excretaAddViewModel.getExcretaDate(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
