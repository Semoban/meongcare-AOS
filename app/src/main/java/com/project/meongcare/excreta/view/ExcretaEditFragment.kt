package com.project.meongcare.excreta.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.BuildConfig
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.EXCRETA_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.model.data.local.PhotoListener
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.utils.EXCRETA_PATCH_FAILURE
import com.project.meongcare.excreta.utils.EXCRETA_PATCH_SUCCESS
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateTimeFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertTimeFormat
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
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ExcretaEditFragment : Fragment(), DateSubmitListener, PhotoListener {
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val excretaPatchViewModel: ExcretaPatchViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val calendarModalBottomSheet = CalendarBottomSheetFragment()

    private lateinit var excretaInfo: ExcretaDetailGetResponse
    private lateinit var imageFile: File
    private lateinit var filePath: String
    private var excretaDate = ""
    private var accessToken = ""
    private var excretaType = Excreta.URINE
    private var excretaTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        calendarModalBottomSheet.setDateSubmitListener(this@ExcretaEditFragment)
        excretaInfo = getExcretaInfo()
        excretaDate = excretaInfo.dateTime.substring(0..9)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val pickedImageUri by excretaPatchViewModel.excretaImage.observeAsState()
                    val pickedDate by excretaPatchViewModel.excretaDate.observeAsState()

                    ExcretaAddEditScreen(
                        imageModel = pickedImageUri ?: excretaInfo.excretaImageURL.takeUnless { it.isNullOrEmpty() },
                        dateText = pickedDate?.let { convertDateFormat(it) } ?: convertDateTimeFormat(excretaInfo.dateTime),
                        initialExcretaType = initialExcretaType(),
                        initialHour = excretaInfo.dateTime.substring(HOUR_START, HOUR_END).toInt(),
                        initialMinute = excretaInfo.dateTime.substring(MINUTE_START, MINUTE_END).toInt(),
                        onBack = { findNavController().popBackStack() },
                        onImageClick = ::showPhotoAttachModalBottomSheet,
                        onDateClick = ::showCalendarModalBottomSheet,
                        onComplete = ::editExcretaInfo,
                    )
                }
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
        }
        observeExcretaDate()
        observeImageUpload()
        observeExcretaPatched()
    }

    private fun initialExcretaType() =
        if (excretaInfo.excretaType == Excreta.FECES.toString()) {
            Excreta.FECES
        } else {
            Excreta.URINE
        }

    private fun observeExcretaDate() {
        excretaPatchViewModel.excretaDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                excretaDate = plusDay(date)
            }
        }
    }

    private fun observeImageUpload() {
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                awsS3ViewModel.uploadImageToS3(response.preSignedUrl, requestBody)
            }
        }
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                patchExcreta(BuildConfig.AWS_S3_BASE_URL + filePath)
            }
        }
    }

    private fun observeExcretaPatched() {
        excretaPatchViewModel.excretaPatched.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
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

    private fun showPhotoAttachModalBottomSheet() {
        val photoAttachModalBottomSheet = PhotoAttachModalBottomSheetFragment()
        photoAttachModalBottomSheet.setPhotoListener(this@ExcretaEditFragment)
        photoAttachModalBottomSheet.show(
            requireActivity().supportFragmentManager,
            PhotoAttachModalBottomSheetFragment.TAG,
        )
    }

    private fun showCalendarModalBottomSheet() {
        calendarModalBottomSheet.show(
            requireActivity().supportFragmentManager,
            calendarModalBottomSheet.tag,
        )
    }

    private fun editExcretaInfo(
        excretaType: Excreta,
        hour: Int,
        minute: Int,
    ) {
        this.excretaType = excretaType
        excretaTime = convertTimeFormat(hour, minute)

        val uri = excretaPatchViewModel.excretaImage.value
        if (uri == null) { // 새로 등록된 이미지가 없을 때
            patchExcreta(excretaInfo.excretaImageURL)
        } else {
            imageFile = convertUriToFile(requireContext(), uri)
            filePath = "$PARENT_FOLDER_PATH$EXCRETA_FOLDER_PATH${imageFile.name}"
            awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        }
    }

    private fun patchExcreta(imageURL: String?) {
        val excretaDateTime = "${excretaDate}T$excretaTime"
        excretaPatchViewModel.patchExcreta(
            accessToken,
            getExcretaId(),
            excretaType.toString(),
            excretaDateTime,
            imageURL,
        )
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
    }
}
