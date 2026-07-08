package com.project.meongcare.info.view

import android.net.Uri
import android.os.Build
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
import com.project.meongcare.databinding.FragmentPetEditBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.home.util.HomeDateUtil.getCurrentDate
import com.project.meongcare.info.model.entities.DogPutRequest
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.view.PhotoSelectBottomSheetFragment
import com.project.meongcare.onboarding.viewmodel.DogTypeSharedViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class PetEditFragment : Fragment(), PhotoMenuListener, DateSubmitListener {
    private var _binding: FragmentPetEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var dogInfo: GetDogInfoResponse
    private lateinit var filePath: String
    private lateinit var imageFile: File

    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val petEditViewModel: ProfileViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()

    private var accessToken = ""
    private var refreshToken = ""
    private var isImageUpdated = false
    private var pendingForm: PetEditFormResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dogInfo = getDogInfo()
        if (savedInstanceState == null) {
            initDogInfo(dogInfo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPetEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchUserInfo()
        observePutResponses()
        observeImageUpload()
        observeReissueResponse()
    }

    private fun initComposeView() {
        binding.composeViewPetEdit.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val imageUri by petEditViewModel.dogProfile.observeAsState()
                    val dogType by dogTypeSharedViewModel.selectedDogType.observeAsState()
                    val birthDate by petEditViewModel.dogBirth.observeAsState()

                    PetEditScreen(
                        initialDogInfo = dogInfo,
                        imageModel = imageUri,
                        dogType = dogType,
                        birthDate = birthDate,
                        onBackClick = { findNavController().popBackStack() },
                        onImageClick = ::showPhotoSelectBottomSheet,
                        onTypeClick = {
                            findNavController().navigate(R.id.action_petEditFragment_to_dogVarietySearchFragment)
                        },
                        onBirthdayClick = ::showBirthdayBottomSheet,
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

    private fun observePutResponses() {
        petEditViewModel.dogPutResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response) {
                    200 -> {
                        val dogPostRequest =
                            WeightPostRequest(
                                dogInfo.dogId,
                                getCurrentDate(),
                                null,
                            )
                        petEditViewModel.postDogWeight(accessToken, dogPostRequest)
                    }
                    401 -> {
                        if (refreshToken.isNotEmpty()) {
                            userViewModel.getNewAccessToken(refreshToken)
                        }
                    }
                    else -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_failure),
                        ).show()
                    }
                }
            }
        }

        petEditViewModel.postDogWeightResponse.observe(viewLifecycleOwner) { postResponse ->
            if (postResponse != null) {
                when (postResponse) {
                    200 -> {
                        petEditViewModel.patchDogWeight(
                            dogInfo.dogId,
                            pendingForm!!.weight,
                            getCurrentDate(),
                            accessToken,
                        )
                    }
                    401 -> {
                        if (refreshToken.isNotEmpty()) {
                            userViewModel.getNewAccessToken(refreshToken)
                        }
                    }
                    else -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_dog_weight_failure),
                        ).show()
                    }
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }

        petEditViewModel.patchDogWeightResponse.observe(viewLifecycleOwner) { patchResponse ->
            if (patchResponse != null) {
                when (patchResponse) {
                    200 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_success_16dp,
                            getString(R.string.snack_bar_dog_edit_complete),
                        ).show()
                        findNavController().popBackStack()
                    }
                    401 -> {
                        if (refreshToken.isNotEmpty()) {
                            userViewModel.getNewAccessToken(refreshToken)
                        }
                    }
                    else -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_dog_weight_failure),
                        ).show()
                    }
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
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                putDogInfo(imageURL)
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
                            getString(R.string.snack_bar_info_edit_failure),
                        ).show()
                        userViewModel.setAccessToken(response.body()?.accessToken)
                    }
                    401 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_refresh_expire),
                        ).show()
                        findNavController().navigate(R.id.action_petEditFragment_to_loginFragment)
                    }
                }
            }
        }
    }

    private fun showPhotoSelectBottomSheet() {
        val modalBottomSheet = PhotoSelectBottomSheetFragment()
        modalBottomSheet.setPhotoMenuListener(this@PetEditFragment)
        modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
        modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
    }

    private fun showBirthdayBottomSheet() {
        val birthdayBottomSheet =
            BirthdayBottomSheetFragment(
                binding.root,
                petEditViewModel.dogBirth.value,
            )
        birthdayBottomSheet.setDateSubmitListener(this@PetEditFragment)
        birthdayBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBirthdayDialogTheme)
        birthdayBottomSheet.show(requireActivity().supportFragmentManager, birthdayBottomSheet.tag)
    }

    private fun submitDogInfo(form: PetEditFormResult) {
        pendingForm = form
        if (isImageUpdated) {
            getPreSignedURL(petEditViewModel.dogProfile.value!!)
        } else {
            putDogInfo(dogInfo.imageUrl)
        }
    }

    private fun getPreSignedURL(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$DOG_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
    }

    private fun uploadImage(
        preSignedURL: String,
        requestBody: RequestBody,
    ) {
        awsS3ViewModel.uploadImageToS3(preSignedURL, requestBody)
    }

    private fun putDogInfo(imageURL: String?) {
        val form = pendingForm ?: return
        val dogPutRequest =
            DogPutRequest(
                form.name,
                dogTypeSharedViewModel.selectedDogType.value!!,
                form.gender,
                petEditViewModel.dogBirth.value!!,
                form.castrate,
                form.weight,
                form.backRound,
                form.neckRound,
                form.chestRound,
                imageURL,
            )

        petEditViewModel.putDogInfo(dogInfo.dogId, accessToken, dogPutRequest)
    }

    private fun getDogInfo() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("dogInfo", GetDogInfoResponse::class.java)!!
        } else {
            arguments?.getParcelable("dogInfo")!!
        }

    private fun initDogInfo(dogInfo: GetDogInfoResponse) {
        if (dogInfo.imageUrl != null) {
            petEditViewModel.setDogProfile(Uri.parse(dogInfo.imageUrl))
        }
        dogTypeSharedViewModel.setDogType(dogInfo.type)
        petEditViewModel.setDogBirth(dogInfo.birthDate)
    }

    override fun onUriPassed(uri: Uri) {
        petEditViewModel.setDogProfile(uri)
        isImageUpdated = true
    }

    override fun onDateSubmit(str: String) {
        petEditViewModel.setDogBirth(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
