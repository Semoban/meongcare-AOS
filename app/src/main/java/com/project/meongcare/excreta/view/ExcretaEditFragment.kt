package com.project.meongcare.excreta.view

import android.net.Uri
import android.os.Build
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
import com.project.meongcare.MainActivity.Companion.AWS_BASE_URL
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.EXCRETA_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentExcretaAddEditBinding
import com.project.meongcare.excreta.model.data.local.PhotoListener
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.utils.EXCRETA_PATCH_FAILURE
import com.project.meongcare.excreta.utils.EXCRETA_PATCH_SUCCESS
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateTimeFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.initCalendarModalBottomSheet
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.plusDay
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showFailureSnackBar
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showSuccessSnackBar
import com.project.meongcare.excreta.utils.HOUR_END
import com.project.meongcare.excreta.utils.HOUR_START
import com.project.meongcare.excreta.utils.MINUTE_END
import com.project.meongcare.excreta.utils.MINUTE_START
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaPatchViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ExcretaEditFragment : Fragment(), DateSubmitListener, PhotoListener {
    private var _binding: FragmentExcretaAddEditBinding? = null
    val binding get() = _binding!!

    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val excretaPatchViewModel: ExcretaPatchViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val calendarModalBottomSheet = CalendarBottomSheetFragment()

    private lateinit var excretaInfo: ExcretaDetailGetResponse
    private lateinit var imageFile: File
    private lateinit var filePath: String
    private var excretaDate = ""
    private var accessToken = ""

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
        calendarModalBottomSheet.setDateSubmitListener(this@ExcretaEditFragment)
        excretaInfo = getExcretaInfo()
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
        }
        initToolbar()
        initDate()
        initExcretaImage()
        initExcretaCheckBox(excretaInfo.excretaType)
        initTime()
        initPhotoAttachModalBottomSheet()
        setUpCalendarModalBottomSheet()
        toggleExcretaCheckboxesOnClick()
        observeAndUpdateExcretaDate()
        editExcretaInfo()
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
            if (!excretaImageURL.isNullOrEmpty()) {
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
        excretaDate = excretaInfo.dateTime.substring(0..9)
        excretaPatchViewModel.excretaDate.observe(viewLifecycleOwner) { date ->
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

    private fun initPhotoAttachModalBottomSheet() {
        binding.cardviewExcretaaddImage.setOnClickListener {
            val photoAttachModalBottomSheet = PhotoAttachModalBottomSheetFragment()
            photoAttachModalBottomSheet.setPhotoListener(this@ExcretaEditFragment)
            photoAttachModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                PhotoAttachModalBottomSheetFragment.TAG,
            )
        }
    }

    private fun editExcretaInfo() {
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
                    val uri = excretaPatchViewModel.excretaImage.value
                    if (uri == null) { // 새로 등록된 이미지가 없을 때
                        if (excretaInfo.excretaImageURL == null) { // 기존 이미지 null
                            patchExcreta(null)
                        } else {
                            patchExcreta(excretaInfo.excretaImageURL)
                        }
                    } else {
                        getPreSignedUrl(uri)
                    }
                }
            }
        }
    }

    private fun getPreSignedUrl(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$EXCRETA_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
    }

    private fun uploadImage(
        preSignedUrl: String,
        requestBody: RequestBody,
    ) {
        awsS3ViewModel.uploadImageToS3(preSignedUrl, requestBody)
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = AWS_BASE_URL + filePath
                patchExcreta(imageURL)
            }
        }
    }

    private fun patchExcreta(imageURL: String?) {
        val excretaType =
            if (binding.checkboxExcretaaddUrine.isChecked) {
                Excreta.URINE.toString()
            } else {
                Excreta.FECES.toString()
            }

        val excretaTime =
            ExcretaDateTimeUtils.convertTimeFormat(binding.timepikerExcretaaddTime)
        val excretaDateTime = "${excretaDate}T$excretaTime"

        excretaPatchViewModel.patchExcreta(
            accessToken,
            getExcretaId(),
            excretaType,
            excretaDateTime,
            imageURL,
        )

        excretaPatchViewModel.excretaPatched.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                showSuccessSnackBar(
                    requireView(),
                    EXCRETA_PATCH_SUCCESS,
                )
                findNavController().popBackStack()
            } else {
                showFailureSnackBar(
                    requireView(),
                    EXCRETA_PATCH_FAILURE,
                )
            }
        }
    }

    private fun getExcretaId() = arguments?.getLong("excretaId")!!

    private fun getExcretaInfo() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("excretaInfo", ExcretaDetailGetResponse::class.java)!!
        } else {
            arguments?.getParcelable("excretaInfo")!!
        }

    override fun onDateSubmit(str: String) {
        excretaPatchViewModel.getExcretaDate(str)
    }

    override fun onUriPassed(uri: Uri) {
        excretaPatchViewModel.getExcretaImage(uri)
        binding.apply {
            Glide.with(this@ExcretaEditFragment)
                .load(uri)
                .into(imageviewExcretaaddPicture)
            includeExcretaAddEditAttachPhoto.root.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
