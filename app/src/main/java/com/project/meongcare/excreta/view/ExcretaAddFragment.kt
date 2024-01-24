package com.project.meongcare.excreta.view

import android.net.Uri
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
import com.project.meongcare.excreta.model.data.local.PhotoListener
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertTimeFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.initCalendarModalBottomSheet
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.plusDay
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaAddViewModel
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExcretaAddFragment : Fragment(), DateSubmitListener, PhotoListener {
    private var _binding: FragmentExcretaAddEditBinding? = null
    val binding get() = _binding!!

    private val excretaAddViewModel: ExcretaAddViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val calendarModalBottomSheet = CalendarBottomSheetFragment()

    private var excretaDate = ""
    private var accessToken = ""
    private var dogId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        calendarModalBottomSheet.setDateSubmitListener(this@ExcretaAddFragment)
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
        }
        dogViewModel.fetchDogId()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        initToolbar()
        initPhotoAttachModalBottomSheet()
        setUpCalendarModalBottomSheet()
        observeAndUpdateExcretaDate()
        toggleExcretaCheckboxesOnClick()
        saveExcretaInfo()
    }

    private fun initToolbar() {
        binding.toolbarExcretaadd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPhotoAttachModalBottomSheet() {
        binding.cardviewExcretaaddImage.setOnClickListener {
            val photoAttachModalBottomSheet = PhotoAttachModalBottomSheetFragment()
            photoAttachModalBottomSheet.setPhotoListener(this@ExcretaAddFragment)
            photoAttachModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                PhotoAttachModalBottomSheetFragment.TAG,
            )
        }
    }

    private fun setUpCalendarModalBottomSheet() {
        binding.apply {
            initCalendarModalBottomSheet(
                textviewExcretaaddDate,
                calendarModalBottomSheet,
                requireActivity(),
                textviewExcretaaddDateError,
            )
        }
    }

    private fun observeAndUpdateExcretaDate(): String {
        excretaAddViewModel.excretaDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                excretaDate = plusDay(date)
                binding.textviewExcretaaddDate.run {
                    setTextColor(resources.getColor(R.color.black, null))
                    setTextAppearance(R.style.Typography_Body1_Medium)
                    text = convertDateFormat(date)
                }
            }
        }
        return excretaDate
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

    override fun onDateSubmit(str: String) {
        excretaAddViewModel.getExcretaDate(str)
    }

    override fun onUriPassed(uri: Uri) {
        excretaAddViewModel.getExcretaImage(uri)
        binding.apply {
            Glide.with(this@ExcretaAddFragment)
                .load(uri)
                .into(imageviewExcretaaddPicture)
            includeExcretaAddEditAttachPhoto.root.visibility = View.INVISIBLE
        }
    }

    private fun saveExcretaInfo() {
        binding.apply {
            buttonExcretaaddCompletion.setOnClickListener {
                var isValid = true

                if (excretaDate.isEmpty()) {
                    textviewExcretaaddDateError.apply {
                        visibility = View.VISIBLE
                    }
                    isValid = false
                }

                if (isValid) {
                    val excretaType =
                        if (checkboxExcretaaddUrine.isChecked) {
                            Excreta.URINE.toString()
                        } else {
                            Excreta.FECES.toString()
                        }

                    val excretaTime = convertTimeFormat(timepikerExcretaaddTime)
                    val excretaDateTime = "${excretaDate}T$excretaTime"

                    val currentImageUri = excretaAddViewModel.excretaImage.value

                    excretaAddViewModel.postExcreta(
                        dogId,
                        accessToken,
                        excretaType,
                        excretaDateTime,
                        requireContext(),
                        currentImageUri ?: Uri.EMPTY,
                    )
                    excretaAddViewModel.excretaPosted.observe(viewLifecycleOwner) { response ->
                        if (response == SUCCESS) {
                            CustomSnackBar.make(
                                requireView(),
                                R.drawable.snackbar_success_16dp,
                                "대소변 정보가 저장되었습니다!"
                            ).show()
                            findNavController().popBackStack()
                        } else {
                            CustomSnackBar.make(
                                requireView(),
                                R.drawable.snackbar_error_16dp,
                                "서버가 불안정 하여 대소변 정보 저장에 실패하였습니다.\n잠시 후 다시 시도해 주세요."
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
