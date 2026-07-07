package com.project.meongcare.excreta.view

import android.net.Uri
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
import com.project.meongcare.excreta.utils.EXCRETA_POST_FAILURE
import com.project.meongcare.excreta.utils.EXCRETA_POST_SUCCESS
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertTimeFormat
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
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ExcretaAddFragment : Fragment(), DateSubmitListener, PhotoListener {
    private val excretaAddViewModel: ExcretaAddViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val calendarModalBottomSheet = CalendarBottomSheetFragment()

    private var excretaDate = ""
    private var accessToken = ""
    private var dogId = 0L
    private var excretaType = Excreta.URINE
    private var excretaTime = ""

    private lateinit var imageFile: File
    private lateinit var filePath: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        calendarModalBottomSheet.setDateSubmitListener(this@ExcretaAddFragment)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val imageUri by excretaAddViewModel.excretaImage.observeAsState()
                    val date by excretaAddViewModel.excretaDate.observeAsState()

                    ExcretaAddEditScreen(
                        imageModel = imageUri,
                        dateText = date?.let { convertDateFormat(it) },
                        initialExcretaType = Excreta.URINE,
                        initialHour = null,
                        initialMinute = null,
                        onBack = { findNavController().popBackStack() },
                        onImageClick = ::showPhotoAttachModalBottomSheet,
                        onDateClick = ::showCalendarModalBottomSheet,
                        onComplete = ::saveExcretaInfo,
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
        dogViewModel.fetchDogId()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        observeExcretaDate()
        observeImageUpload()
        observeExcretaPosted()
    }

    private fun observeExcretaDate() {
        excretaAddViewModel.excretaDate.observe(viewLifecycleOwner) { date ->
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
                postExcreta(BuildConfig.AWS_S3_BASE_URL + filePath)
            }
        }
    }

    private fun observeExcretaPosted() {
        excretaAddViewModel.excretaPosted.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
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

    private fun showPhotoAttachModalBottomSheet() {
        val photoAttachModalBottomSheet = PhotoAttachModalBottomSheetFragment()
        photoAttachModalBottomSheet.setPhotoListener(this@ExcretaAddFragment)
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

    private fun saveExcretaInfo(
        excretaType: Excreta,
        hour: Int,
        minute: Int,
    ) {
        this.excretaType = excretaType
        excretaTime = convertTimeFormat(hour, minute)

        val uri = excretaAddViewModel.excretaImage.value
        if (uri == null) {
            postExcreta(null)
        } else {
            imageFile = convertUriToFile(requireContext(), uri)
            filePath = "$PARENT_FOLDER_PATH$EXCRETA_FOLDER_PATH${imageFile.name}"
            awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        }
    }

    private fun postExcreta(imageURL: String?) {
        val excretaDateTime = "${excretaDate}T$excretaTime"
        excretaAddViewModel.postExcreta(
            accessToken,
            dogId,
            excretaType.toString(),
            excretaDateTime,
            imageURL,
        )
    }

    override fun onDateSubmit(str: String) {
        excretaAddViewModel.getExcretaDate(str)
    }

    override fun onUriPassed(uri: Uri) {
        excretaAddViewModel.getExcretaImage(uri)
    }
}
