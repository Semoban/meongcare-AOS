package com.project.meongcare.onboarding.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.BirthdayBottomSheetFragment
import com.project.meongcare.BuildConfig
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.DOG_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.model.entities.DogPostRequest
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import com.project.meongcare.onboarding.viewmodel.DogTypeSharedViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class DogAddOnBoardingFragment : Fragment(), PhotoMenuListener, DateSubmitListener {
    private var _binding: FragmentDogAddOnBoardingBinding? = null
    private val binding get() = _binding!!

    private val dogAddViewModel: DogAddViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()

    private var isFirstRegister: Boolean? = null
    private var pendingForm: DogAddFormResult? = null

    private var accessToken = ""
    private var refreshToken = ""
    private lateinit var imageFile: File
    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstRegister = arguments?.getBoolean("isFirstRegister")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDogAddOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchUserInfo()
        observeDogAddResponse()
        observeImageUpload()
        observeReissueResponse()
    }

    private fun initComposeView() {
        binding.composeViewDogAddOnBoarding.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val imageUri by dogAddViewModel.dogProfileImage.observeAsState()
                    val dogType by dogTypeSharedViewModel.selectedDogType.observeAsState()
                    val birthDate by dogAddViewModel.dogBirthDate.observeAsState()

                    DogAddOnBoardingScreen(
                        imageModel = imageUri,
                        dogType = dogType,
                        birthDate = birthDate,
                        showCancelButton = isFirstRegister != true,
                        onImageClick = ::showPhotoSelectBottomSheet,
                        onTypeClick = {
                            findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_dogVarietySearchFragment)
                        },
                        onBirthdayClick = ::showBirthdayBottomSheet,
                        onCancelClick = { findNavController().popBackStack() },
                        onComplete = ::submitDogInfo,
                    )
                }
            }
        }
    }

    private fun fetchUserInfo() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
            }
        }
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                this.refreshToken = refreshToken
            }
        }
    }

    private fun observeDogAddResponse() {
        dogAddViewModel.dogAddResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    getString(R.string.snack_bar_dog_create_complete),
                ).show()
                userViewModel.setIsFirstLogin(false)
                findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_completeOnBoardingFragment)
            } else if (response == 401) {
                if (refreshToken.isNotEmpty()) {
                    userViewModel.getNewAccessToken(refreshToken)
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
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
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                postDogInfo(imageURL)
            }
        }
    }

    private fun observeReissueResponse() {
        userViewModel.reissueResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response.code()) {
                    200 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_info_add_failure),
                        ).show()
                        userViewModel.setAccessToken(response.body()?.accessToken)
                    }
                    401 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_refresh_expire),
                        ).show()
                        findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_loginFragment)
                    }
                }
            }
        }
    }

    private fun showPhotoSelectBottomSheet() {
        val modalBottomSheet = PhotoSelectBottomSheetFragment()
        modalBottomSheet.setPhotoMenuListener(this@DogAddOnBoardingFragment)
        modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
        modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
    }

    private fun showBirthdayBottomSheet() {
        val birthdayBottomSheet =
            BirthdayBottomSheetFragment(
                binding.root,
                dogAddViewModel.dogBirthDate.value,
            )
        birthdayBottomSheet.setDateSubmitListener(this@DogAddOnBoardingFragment)
        birthdayBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBirthdayDialogTheme)
        birthdayBottomSheet.show(requireActivity().supportFragmentManager, birthdayBottomSheet.tag)
    }

    private fun submitDogInfo(form: DogAddFormResult) {
        pendingForm = form
        val uri = dogAddViewModel.dogProfileImage.value
        if (uri == null) {
            postDogInfo(null)
        } else {
            imageFile = convertUriToFile(requireContext(), uri)
            filePath = "$PARENT_FOLDER_PATH$DOG_FOLDER_PATH${imageFile.name}"
            awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        }
    }

    private fun postDogInfo(imageURL: String?) {
        val form = pendingForm ?: return
        val dogPostRequest =
            DogPostRequest(
                form.name,
                dogTypeSharedViewModel.selectedDogType.value!!,
                form.gender,
                dogAddViewModel.dogBirthDate.value!!,
                form.castrate,
                form.weight,
                form.backRound,
                form.neckRound,
                form.chestRound,
                imageURL,
            )

        if (accessToken.isNotEmpty()) {
            dogAddViewModel.postDogInfo(
                accessToken,
                dogPostRequest,
            )
        }
    }

    override fun onUriPassed(uri: Uri) {
        dogAddViewModel.getDogProfileImage(uri)
    }

    override fun onDateSubmit(str: String) {
        dogAddViewModel.getDogBirthDate(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
