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
import com.project.meongcare.BuildConfig
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.EXCRETA_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentExcretaAddEditBinding
import com.project.meongcare.excreta.model.data.local.PhotoListener
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.utils.EXCRETA_POST_FAILURE
import com.project.meongcare.excreta.utils.EXCRETA_POST_SUCCESS
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertTimeFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.initCalendarModalBottomSheet
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.plusDay
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showFailureSnackBar
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showSuccessSnackBar
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaAddViewModel
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ExcretaAddFragment : Fragment(), DateSubmitListener, PhotoListener {
    private var _binding: FragmentExcretaAddEditBinding? = null
    val binding get() = _binding!!

    private val excretaAddViewModel: ExcretaAddViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val calendarModalBottomSheet = CalendarBottomSheetFragment()

    private var excretaDate = ""
    private var accessToken = ""
    private var dogId = 0L

    private lateinit var imageFile: File
    private lateinit var filePath: String

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
                    val uri = excretaAddViewModel.excretaImage.value
                    if (uri == null) {
                        postExcreta(null)
                    } else {
                        imageFile = convertUriToFile(requireContext(), uri)
                        filePath = "$PARENT_FOLDER_PATH$EXCRETA_FOLDER_PATH${imageFile.name}"
                        getPreSignedUrl()
                    }
                }
            }
        }
    }

    private fun getPreSignedUrl() {
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
    }

    private fun uploadImage(preSignedUrl: String, requestBody: RequestBody) {
        awsS3ViewModel.uploadImageToS3(preSignedUrl, requestBody)
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                postExcreta(imageURL)
            }
        }
    }

    private fun postExcreta(imageURL: String?) {
        val excretaType =
            if (binding.checkboxExcretaaddUrine.isChecked) {
                Excreta.URINE.toString()
            } else {
                Excreta.FECES.toString()
            }

        val excretaTime = convertTimeFormat(binding.timepikerExcretaaddTime)
        val excretaDateTime = "${excretaDate}T$excretaTime"

        excretaAddViewModel.postExcreta(
            accessToken,
            dogId,
            excretaType,
            excretaDateTime,
            imageURL,
        )
        excretaAddViewModel.excretaPosted.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                showSuccessSnackBar(
                    requireView(),
                    EXCRETA_POST_SUCCESS,
                )
                findNavController().popBackStack()
            } else {
                showFailureSnackBar(
                    requireView(),
                    EXCRETA_POST_FAILURE,
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
