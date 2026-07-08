package com.project.meongcare.supplement.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.BuildConfig
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.util.SUPPLEMENTS_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentSupplementAddBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.supplement.model.data.local.OnPictureChangedListener
import com.project.meongcare.supplement.model.entities.SupplementPostRequest
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.showCycleBottomSheet
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.showTimeBottomSheet
import com.project.meongcare.supplement.view.bottomSheet.SupplementPictureBottomSheetDialogFragment
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class SupplementAddFragment : Fragment(), OnPictureChangedListener {
    private var _binding: FragmentSupplementAddBinding? = null
    private val binding get() = _binding!!

    private val supplementViewModel: SupplementViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    private lateinit var imageFile: File
    private lateinit var filePath: String

    private var accessToken = ""
    private var dogId = 0L
    private var brand = ""
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSupplementAddBinding.inflate(inflater, container, false)
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
        observeSupplementCode()
    }

    private fun initComposeView() {
        binding.composeViewSupplementAdd.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val imageUri by supplementViewModel.supplementAddImg.observeAsState()
                    val cycle by supplementViewModel.supplementCycle.observeAsState()
                    val intakeUnit by supplementViewModel.intakeTimeUnit.observeAsState()
                    val intakeTimeList by supplementViewModel.intakeTimeList.observeAsState()

                    SupplementAddScreen(
                        imageModel = imageUri,
                        cycle = cycle,
                        intakeUnit = intakeUnit ?: "mg",
                        intakeTimeList = intakeTimeList.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                        onImageClick = ::showPictureBottomSheet,
                        onCycleClick = { showCycleBottomSheet(parentFragmentManager, supplementViewModel) },
                        onUnitSelect = supplementViewModel::updateIntakeTimeUnit,
                        onTimeAddClick = { showTimeBottomSheet(parentFragmentManager, supplementViewModel) },
                        onTimeRemove = supplementViewModel::removeIntakeTimeListItem,
                        onComplete = ::postSupplementWithImage,
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

    private fun observeImageUpload() {
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                postSupplement(imageURL)
            }
        }
    }

    private fun observeSupplementCode() {
        supplementViewModel.supplementCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                showSuccessSnackbar()
                findNavController().popBackStack()
            } else {
                showFailSnackbar()
            }
        }
    }

    private fun postSupplementWithImage(
        brand: String,
        name: String,
    ) {
        this.brand = brand
        this.name = name
        val uri = supplementViewModel.supplementAddImg.value
        if (uri == null) {
            postSupplement(null)
        } else {
            getPreSignedUrl(uri)
        }
    }

    private fun getPreSignedUrl(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$SUPPLEMENTS_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
    }

    private fun uploadImage(
        preSignedUrl: String,
        requestBody: RequestBody,
    ) {
        awsS3ViewModel.uploadImageToS3(preSignedUrl, requestBody)
    }

    private fun postSupplement(imageURL: String?) {
        supplementViewModel.addSupplement(
            accessToken,
            createSupplementInfo(imageURL),
        )
    }

    private fun createSupplementInfo(imageURL: String?): SupplementPostRequest {
        val intakeCycle = supplementViewModel.supplementCycle.value!!
        val intakeUnit = supplementViewModel.intakeTimeUnit.value!!
        val intakeInfos = supplementViewModel.intakeTimeList.value!!
        return SupplementPostRequest(dogId, brand, name, intakeCycle, intakeUnit, imageURL, intakeInfos)
    }

    private fun showPictureBottomSheet() {
        val bottomSheetFragment = SupplementPictureBottomSheetDialogFragment()

        bottomSheetFragment.setOnPictureChangedListener(this)

        bottomSheetFragment.show(
            parentFragmentManager,
            "SupplementPictureBottomSheetDialogFragment",
        )
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            "추가가 완료되었습니다",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            "추가에 실패하였습니다.\n잠시 후 다시 시도해주세요",
        ).show()
    }

    override fun onPictureChanged(uri: Uri) {
        supplementViewModel.supplementAddImg.value = uri
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
