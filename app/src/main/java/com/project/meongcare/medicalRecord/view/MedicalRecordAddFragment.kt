package com.project.meongcare.medicalRecord.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.BuildConfig
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.MEDICAL_RECORD_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentMedicalRecordAddBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.model.data.local.OnPictureChangedListener
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordDateBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordPictureBottomSheetDialogFragment
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MedicalRecordAddFragment :
    Fragment(),
    MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener,
    OnPictureChangedListener {
    private var _binding: FragmentMedicalRecordAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var filePath: String
    private lateinit var imageFile: File

    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    private val selectedDateState = mutableStateOf<String?>(null)

    private var accessToken = ""
    private var dogId = -1L
    private var medicalRecordForm: MedicalRecordFormResult? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicalRecordAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchUserAndDogInfo()
        observeImageUpload()
        observeMedicalRecordResponse()
    }

    private fun initComposeView() {
        binding.composeViewMedicalRecordAdd.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val imageUri by medicalRecordViewModel.medicalRecordAddImgUri.observeAsState()
                    val selectedDate by selectedDateState

                    MedicalRecordFormScreen(
                        initialRecord = null,
                        imageModel = imageUri?.takeIf { it != Uri.EMPTY },
                        selectedDate = selectedDate,
                        completeText = getString(R.string.medicalrecord_record),
                        onBackClick = { findNavController().popBackStack() },
                        onImageClick = ::showPictureBottomSheet,
                        onDateClick = ::showCalendarBottomSheet,
                        onComplete = ::submitMedicalRecord,
                    )
                }
            }
        }
    }

    private fun fetchUserAndDogInfo() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
            }
        }
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
            }
        }
    }

    private fun submitMedicalRecord(form: MedicalRecordFormResult) {
        if (accessToken.isEmpty() || dogId == -1L) return

        medicalRecordForm = form
        val uri = medicalRecordViewModel.medicalRecordAddImgUri.value
        if (uri == null || uri == Uri.EMPTY) {
            postMedicalRecord(null)
        } else {
            imageFile = convertUriToFile(requireContext(), uri)
            filePath = "$PARENT_FOLDER_PATH$MEDICAL_RECORD_FOLDER_PATH${imageFile.name}"
            awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        }
    }

    private fun postMedicalRecord(imageURL: String?) {
        val form = medicalRecordForm ?: return
        val time = String.format("%02d:%02d:00", form.hour, form.minute)
        val dateTime = "${form.date}T$time"

        medicalRecordViewModel.addMedicalRecord(
            accessToken,
            dogId,
            dateTime,
            form.hospitalName,
            form.doctorName,
            form.note,
            imageURL,
        )
    }

    private fun observeImageUpload() {
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                awsS3ViewModel.uploadImageToS3(response.preSignedUrl, requestBody)
            }
        }
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                postMedicalRecord(imageURL)
            }
        }
    }

    private fun observeMedicalRecordResponse() {
        medicalRecordViewModel.medicalRecordResponse.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response == 200) {
                findNavController().popBackStack()
                CustomSnackBar.make(
                    activity?.findViewById(android.R.id.content)!!,
                    R.drawable.snackbar_success_16dp,
                    "추가가 완료되었습니다",
                ).show()
            } else {
                CustomSnackBar.make(
                    activity?.findViewById(android.R.id.content)!!,
                    R.drawable.snackbar_error_16dp,
                    "추가에 실패하였습니다.\n잠시 후 다시 시도해주세요",
                ).show()
            }
        }
    }

    private fun showPictureBottomSheet() {
        val bottomSheetFragment = MedicalRecordPictureBottomSheetDialogFragment()
        bottomSheetFragment.setOnPictureChangedListener(this)
        bottomSheetFragment.show(
            parentFragmentManager,
            "MedicalRecordPictureBottomSheetDialogFragment",
        )
    }

    private fun showCalendarBottomSheet() {
        val bottomSheetDialogFragment = MedicalRecordDateBottomSheetDialogFragment()
        bottomSheetDialogFragment.setOnDateSelecetedListener(this)
        bottomSheetDialogFragment.show(
            parentFragmentManager,
            "MedicalRecordDateBottomSheetDialogFragment",
        )
    }

    override fun onDateSelected(date: LocalDate) {
        selectedDateState.value = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    override fun onPictureChanged(uri: Uri) {
        medicalRecordViewModel.getMedicalRecordImgUri(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
