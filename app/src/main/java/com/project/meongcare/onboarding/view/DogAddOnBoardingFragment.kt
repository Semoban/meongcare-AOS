package com.project.meongcare.onboarding.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.BirthdayBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.aws.util.DOG_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.util.AWSS3ImageUtils.createMultipartFromUri
import com.project.meongcare.aws.util.AWSS3ImageUtils.getMultipartFileName
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding
import com.project.meongcare.medicalrecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.model.entities.DogPostRequest
import com.project.meongcare.onboarding.util.DogAddOnBoardingDateUtils.dateFormat
import com.project.meongcare.onboarding.util.DogAddOnBoardingInfoUtils.bodySizeCheck
import com.project.meongcare.onboarding.util.DogAddOnBoardingInfoUtils.getCheckedGender
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import com.project.meongcare.onboarding.viewmodel.DogTypeSharedViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class DogAddOnBoardingFragment : Fragment(), PhotoMenuListener, DateSubmitListener {
    lateinit var binding: FragmentDogAddOnBoardingBinding

    private val dogAddViewModel: DogAddViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()

    private var isCbxChecked = false
    private var isFirstRegister: Boolean? = null

    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var multipartImage: MultipartBody.Part
    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstRegister = arguments?.getBoolean("isFirstRegister")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDogAddOnBoardingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onUriPassed(uri: Uri) {
        dogAddViewModel.getDogProfileImage(uri)
    }

    override fun onDateSubmit(str: String) {
        dogAddViewModel.getDogBirthDate(str)
    }

    private fun initViews() {
        // 사진 등록
        binding.cardviewPetaddImage.setOnClickListener {
            val modalBottomSheet = PhotoSelectBottomSheetFragment()
            modalBottomSheet.setPhotoMenuListener(this@DogAddOnBoardingFragment)
            modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
            modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
        }

        // 품종 등록
        binding.viewPetaddType.setOnClickListener {
            findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_dogVarietySearchFragment)
        }

        // 날짜 등록
        binding.textviewPetaddSelectBirthday.setOnClickListener {
            val birthdayBottomSheet =
                BirthdayBottomSheetFragment(
                    binding.root,
                    dogAddViewModel.dogBirthDate.value,
                )
            birthdayBottomSheet.setDateSubmitListener(this@DogAddOnBoardingFragment)
            birthdayBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBirthdayDialogTheme)
            birthdayBottomSheet.show(requireActivity().supportFragmentManager, birthdayBottomSheet.tag)
        }

        binding.checkboxPetaddNeuterStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            isCbxChecked = isChecked
        }

        // 중성화 여부 텍스트 클릭 시 체크박스 반전
        binding.textviewPetaddNeuterStatus.setOnClickListener {
            binding.checkboxPetaddNeuterStatus.isChecked = !isCbxChecked
        }

        binding.edittextPetaddNameError.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.edittextPetaddName.requestFocus()
        }

        binding.edittextPetaddWeightError.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.edittextPetaddWeight.requestFocus()
        }

        binding.edittextPetaddSelectTypeError.setOnClickListener {
            findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_dogVarietySearchFragment)
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        initCancelButtonVisibility()
        initCompleteButton()
    }

    private fun initObserves() {
        dogAddViewModel.dogProfileImage.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.run {
                    Glide.with(this@DogAddOnBoardingFragment)
                        .load(uri)
                        .into(imageviewPetaddImage)
                    imageviewPetaddDog.visibility = View.GONE
                    textviewPetaddImageDescription.visibility = View.GONE
                }
            }
        }

        dogAddViewModel.dogBirthDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                binding.textviewPetaddSelectBirthday.run {
                    binding.edittextPetaddSelectBirthdayError.visibility = View.INVISIBLE
                    text = dateFormat(date)
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }
        }

        dogTypeSharedViewModel.selectedDogType.observe(viewLifecycleOwner) { dogType ->
            if (dogType != null) {
                binding.edittextPetaddSelectType.run {
                    binding.edittextPetaddSelectTypeError.visibility = View.GONE
                    text = dogType
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }
        }

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
                    reissueAccessToken()
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }

        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                awsS3ViewModel.uploadImageToS3(response.preSignedUrl, multipartImage)
            }
        }

        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val name = binding.edittextPetaddName.text.toString()
                val type = binding.edittextPetaddSelectType.text.toString()
                val sex = getCheckedGender(binding.root, binding.chipgroupPetaddGroupGender.checkedChipId)
                val birthDate = dogAddViewModel.dogBirthDate.value!!
                val castrate = binding.checkboxPetaddNeuterStatus.isChecked
                val weight: Double = binding.edittextPetaddWeight.text.toString().toDouble()
                val backRound: Double? = bodySizeCheck(binding.edittextPetaddBackLength.text.toString())
                val neckRound: Double? = bodySizeCheck(binding.edittextPetaddNeckCircumference.text.toString())
                val chestRound: Double? = bodySizeCheck(binding.edittextPetaddChestCircumference.text.toString())

                val dogPostRequest =
                    DogPostRequest(
                        name,
                        type,
                        sex,
                        birthDate,
                        castrate,
                        weight,
                        backRound,
                        neckRound,
                        chestRound,
                        filePath,
                    )

                if (accessToken.isNotEmpty()) {
                    dogAddViewModel.postDogInfo(
                        accessToken,
                        dogPostRequest,
                    )
                }
            }
        }
    }

    private fun initCancelButtonVisibility() {
        when (isFirstRegister) {
            true -> binding.buttonCancel.visibility = View.GONE
            false, null -> binding.buttonCancel.visibility = View.VISIBLE
        }
    }

    private fun initCompleteButton() {
        binding.buttonComplete.setOnClickListener {
            // 입력 검사
            if (binding.edittextPetaddName.text.isEmpty()) {
                binding.edittextPetaddNameError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (binding.edittextPetaddSelectType.text.isEmpty()) {
                binding.edittextPetaddSelectTypeError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (binding.chipgroupPetaddGroupGender.checkedChipId == View.NO_ID) {
                return@setOnClickListener
            }

            if (binding.textviewPetaddSelectBirthday.text.isEmpty()) {
                binding.edittextPetaddSelectBirthdayError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (binding.edittextPetaddWeight.text.isEmpty()) {
                binding.edittextPetaddWeightError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            multipartImage = createMultipartFromUri(requireContext(), dogAddViewModel.dogProfileImage.value)
            val fileName = getMultipartFileName(multipartImage)
            filePath = "$PARENT_FOLDER_PATH$DOG_FOLDER_PATH$fileName"
            awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        }
    }
}

fun createMultipartBody(
    context: Context,
    uri: Uri?,
): MultipartBody.Part {
    if (uri != null) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "tempFile")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
    val emptyBody = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", "", emptyBody)
}
