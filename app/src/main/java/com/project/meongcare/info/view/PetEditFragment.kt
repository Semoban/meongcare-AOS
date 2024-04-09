package com.project.meongcare.info.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.BirthdayBottomSheetFragment
import com.project.meongcare.BuildConfig
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.DOG_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentPetEditBinding
import com.project.meongcare.info.model.entities.DogPutRequest
import com.project.meongcare.home.util.HomeDateUtil.getCurrentDate
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.model.entities.Gender
import com.project.meongcare.onboarding.util.DogAddOnBoardingDateUtils.dateFormat
import com.project.meongcare.onboarding.util.DogAddOnBoardingInfoUtils.bodySizeCheck
import com.project.meongcare.onboarding.util.DogAddOnBoardingInfoUtils.getCheckedGender
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
    private lateinit var binding: FragmentPetEditBinding
    private lateinit var dogInfo: GetDogInfoResponse
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var filePath: String
    private lateinit var imageFile: File

    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val petEditViewModel: ProfileViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()

    private var isCbxChecked = false
    private var isInitialized = false
    private var isImageUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dogInfo = getDogInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPetEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isInitialized) {
            initDogInfo(dogInfo)
            isInitialized = true
        }

        getAccessToken()
        initObserves()
        initViews()
    }

    private fun getAccessToken() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                getRefreshToken()
            }
        }
    }

    private fun getRefreshToken() {
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                this.refreshToken = refreshToken
            }
        }
    }

    private fun reissueAccessToken() {
        userViewModel.getNewAccessToken(refreshToken)
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

    private fun initObserves() {
        petEditViewModel.dogProfile.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Glide.with(this@PetEditFragment)
                    .load(uri)
                    .error(R.drawable.dog_profile_default)
                    .into(binding.imageviewPeteditImage)
            }
        }

        petEditViewModel.dogBirth.observe(viewLifecycleOwner) { birth ->
            if (birth != null) {
                binding.edittextPeteditSelectBirthday.setText(dateFormat(birth))
            }
        }

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
                            reissueAccessToken()
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
                        // post 성공, patch 호출
                        petEditViewModel.patchDogWeight(
                            dogInfo.dogId,
                            binding.edittextPeteditWeight.text.toString().toDouble(),
                            getCurrentDate(),
                            accessToken,
                        )
                    }
                    401 -> {
                        if (refreshToken.isNotEmpty()) {
                            reissueAccessToken()
                        }
                    }
                    else -> {
                        // 강아지 정보 수정 실패
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
                            reissueAccessToken()
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

        dogTypeSharedViewModel.selectedDogType.observe(viewLifecycleOwner) { dogType ->
            if (dogType != null) {
                binding.edittextPeteditType.setText(dogType)
            }
        }
    }

    private fun initViews() {
        editTextWatcher(binding.edittextPeteditName, binding.edittextPeteditName, "이름을 입력해주세요")
        editTextWatcher(binding.edittextPeteditType, binding.edittextPeteditType, "품종을 입력해주세요")
        editTextWatcher(binding.edittextPeteditSelectBirthday, binding.edittextPeteditSelectBirthday, "날짜를 선택해주세요")
        editTextWatcher(binding.edittextPeteditWeight, binding.viewPeteditWeight, "")

        binding.cardviewPeteditImage.setOnClickListener {
            val modalBottomSheet = PhotoSelectBottomSheetFragment()
            modalBottomSheet.setPhotoMenuListener(this@PetEditFragment)
            modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
            modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
        }

        binding.edittextPeteditType.setOnClickListener {
            findNavController().navigate(R.id.action_petEditFragment_to_dogVarietySearchFragment)
        }

        binding.checkboxPeteditNeuterStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            isCbxChecked = isChecked
        }

        binding.textviewPeteditNeuterStatus.setOnClickListener {
            binding.checkboxPeteditNeuterStatus.isChecked = !isCbxChecked
        }

        binding.edittextPeteditSelectBirthday.setOnClickListener {
            val birthdayBottomSheet =
                BirthdayBottomSheetFragment(
                    binding.root,
                    petEditViewModel.dogBirth.value,
                )
            birthdayBottomSheet.setDateSubmitListener(this@PetEditFragment)
            birthdayBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBirthdayDialogTheme)
            birthdayBottomSheet.show(requireActivity().supportFragmentManager, birthdayBottomSheet.tag)
        }

        binding.buttonPeteditCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonPeteditSubmit.setOnClickListener {
            if (binding.edittextPeteditName.text.isEmpty()) {
                return@setOnClickListener
            }
            if (binding.edittextPeteditType.text.isEmpty()) {
                return@setOnClickListener
            }
            if (binding.edittextPeteditSelectBirthday.text.isEmpty()) {
                return@setOnClickListener
            }
            if (binding.edittextPeteditWeight.text.isEmpty()) {
                return@setOnClickListener
            }

            if (isImageUpdated) {// 새 이미지 등록
                getPreSignedURL(petEditViewModel.dogProfile.value!!)
            } else { // 기존 이미지
                putDogInfo(dogInfo.imageUrl)
            }
        }
    }

    private fun getPreSignedURL(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$DOG_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
    }

    private fun uploadImage(preSignedURL: String, requestBody: RequestBody) {
        awsS3ViewModel.uploadImageToS3(preSignedURL, requestBody)
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                putDogInfo(imageURL)
            }
        }
    }

    private fun putDogInfo(imageURL: String?) {
        val dogName = binding.edittextPeteditName.text.toString()
        val dogType = binding.edittextPeteditType.text.toString()
        val dogGender = getCheckedGender(binding.root, binding.chipgroupPeteditGroupGender.checkedChipId)
        val dogBirth = petEditViewModel.dogBirth.value!!
        val dogCastrate = binding.checkboxPeteditNeuterStatus.isChecked
        val dogWeight = binding.edittextPeteditWeight.text.toString().toDouble()
        val dogBack = bodySizeCheck(binding.edittextPeteditBackLength.text.toString())
        val dogNeck = bodySizeCheck(binding.edittextPeteditNeckCircumference.text.toString())
        val dogChest = bodySizeCheck(binding.edittextPeteditChestCircumference.text.toString())
        val dogPutRequest =
            DogPutRequest(
                dogName,
                dogType,
                dogGender,
                dogBirth,
                dogCastrate,
                dogWeight,
                dogBack,
                dogNeck,
                dogChest,
                imageURL,
            )

        petEditViewModel.putDogInfo(dogInfo.dogId, accessToken, dogPutRequest)
    }

    private fun editTextWatcher(
        editText: EditText,
        targetView: View,
        hint: String,
    ) {
        editText.addTextChangedListener {
            editText.doAfterTextChanged {
                updateEditTextStyle(editText, targetView, hint)
            }
        }
    }

    private fun updateEditTextStyle(
        editText: EditText,
        targetView: View,
        hint: String,
    ) {
        if (editText.text.isNullOrEmpty()) {
            targetView.setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
            editText.hint = "필수 입력 값입니다"
            editText.setHintTextColor(requireContext().getColor(R.color.sub1))
        } else {
            targetView.setBackgroundResource(R.drawable.all_rect_r5)
            editText.hint = hint
            editText.setHintTextColor(requireContext().getColor(R.color.gray4))
        }
    }

    private fun getDogInfo() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("dogInfo", GetDogInfoResponse::class.java)!!
        } else {
            arguments?.getParcelable("dogInfo")!!
        }

    private fun initDogInfo(dogInfo: GetDogInfoResponse) {
        binding.run {
            if (dogInfo.imageUrl != null) {
                val imageUri = Uri.parse(dogInfo.imageUrl)
                petEditViewModel.setDogProfile(imageUri)
            }
            edittextPeteditName.setText(dogInfo.name)
            dogTypeSharedViewModel.setDogType(dogInfo.type)
            when (dogInfo.sex) {
                Gender.FEMALE.english -> chipgroupPeteditGroupGender.check(R.id.chip_petedit_female)
                Gender.MALE.english -> chipgroupPeteditGroupGender.check(R.id.chip_petedit_male)
            }
            checkboxPeteditNeuterStatus.isChecked = dogInfo.castrate
            isCbxChecked = dogInfo.castrate
            petEditViewModel.setDogBirth(dogInfo.birthDate)
            edittextPeteditWeight.setText(dogInfo.weight.toString())
            if (dogInfo.backRound != null && dogInfo.backRound != 0.0) {
                edittextPeteditBackLength.setText(dogInfo.backRound.toString())
            }
            if (dogInfo.chestRound != null && dogInfo.chestRound != 0.0) {
                edittextPeteditChestCircumference.setText(dogInfo.chestRound.toString())
            }
            if (dogInfo.neckRound != null && dogInfo.neckRound != 0.0) {
                edittextPeteditNeckCircumference.setText(dogInfo.neckRound.toString())
            }
        }
    }

    override fun onUriPassed(uri: Uri) {
        petEditViewModel.setDogProfile(uri)
        isImageUpdated = true
    }

    override fun onDateSubmit(str: String) {
        petEditViewModel.setDogBirth(str)
    }
}
