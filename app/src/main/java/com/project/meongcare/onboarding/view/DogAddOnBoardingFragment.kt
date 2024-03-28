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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.project.meongcare.BirthdayBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.model.entities.Dog
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import com.project.meongcare.onboarding.viewmodel.DogTypeSharedViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class DogAddOnBoardingFragment : Fragment(), PhotoMenuListener, DateSubmitListener {
    lateinit var binding: FragmentDogAddOnBoardingBinding

    private val dogAddViewModel: DogAddViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()
    private val postDogCoroutineJob = Job()
    private val dogAddCoroutineJob = Job()

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var loginRepository: LoginRepository

    private var isCbxChecked = false
    private var isFirstRegister: Boolean? = null

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
            postDogCoroutineJob.cancel()
            if (response == 200) {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    getString(R.string.snack_bar_dog_create_complete),
                ).show()
                userPreferences.setIsFirstLogin(false)
                findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_completeOnBoardingFragment)
            } else if (response == 401) {
                lifecycleScope.launch(dogAddCoroutineJob) {
                    val refreshToken = userPreferences.getRefreshToken()
                    if (refreshToken.isNotEmpty()) {
                        val reissueResponse = loginRepository.getNewAccessToken(refreshToken)
                        if (reissueResponse != null && reissueResponse.code() == 200) {
                            CustomSnackBar.make(
                                requireView(),
                                R.drawable.snackbar_error_16dp,
                                getString(R.string.snack_bar_info_add_failure),
                            ).show()
                            userPreferences.setAccessToken(reissueResponse.body()?.accessToken!!)
                        } else if (reissueResponse != null && reissueResponse.code() == 401) {
                            CustomSnackBar.make(
                                requireView(),
                                R.drawable.snackbar_error_16dp,
                                getString(R.string.snack_bar_refresh_expire),
                            ).show()
                            findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_loginFragment)
                        }
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

        binding.run {
            when (isFirstRegister) {
                true -> buttonCancel.visibility = View.GONE
                false, null -> buttonCancel.visibility = View.VISIBLE
            }

            // 사진 등록
            cardviewPetaddImage.setOnClickListener {
                val modalBottomSheet = PhotoSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@DogAddOnBoardingFragment)
                // 둥근 모서리 지정
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
            }

            // 품종 등록
            viewPetaddType.setOnClickListener {
                findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_dogVarietySearchFragment)
            }

            // 날짜 등록
            textviewPetaddSelectBirthday.setOnClickListener {
                val birthdayBottomSheet =
                    BirthdayBottomSheetFragment(
                        binding.root,
                        dogAddViewModel.dogBirthDate.value,
                    )
                birthdayBottomSheet.setDateSubmitListener(this@DogAddOnBoardingFragment)
                birthdayBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBirthdayDialogTheme)
                birthdayBottomSheet.show(requireActivity().supportFragmentManager, birthdayBottomSheet.tag)
            }

            checkboxPetaddNeuterStatus.setOnCheckedChangeListener { buttonView, isChecked ->
                isCbxChecked = isChecked
            }

            // 중성화 여부 텍스트 클릭 시 체크박스 반전
            textviewPetaddNeuterStatus.setOnClickListener {
                checkboxPetaddNeuterStatus.isChecked = !isCbxChecked
            }

            edittextPetaddNameError.setOnClickListener {
                it.visibility = View.INVISIBLE
                edittextPetaddName.requestFocus()
            }
            edittextPetaddWeightError.setOnClickListener {
                it.visibility = View.INVISIBLE
                edittextPetaddWeight.requestFocus()
            }
            edittextPetaddSelectTypeError.setOnClickListener {
                findNavController().navigate(R.id.action_dogAddOnBoardingFragment_to_dogVarietySearchFragment)
            }

            buttonCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            // 완료
            buttonComplete.setOnClickListener {
                // 입력 검사
                if (edittextPetaddName.text.isEmpty()) {
                    edittextPetaddNameError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (edittextPetaddSelectType.text.isEmpty()) {
                    edittextPetaddSelectTypeError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (chipgroupPetaddGroupGender.checkedChipId == View.NO_ID) {
                    return@setOnClickListener
                }

                if (textviewPetaddSelectBirthday.text.isEmpty()) {
                    edittextPetaddSelectBirthdayError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (edittextPetaddWeight.text.isEmpty()) {
                    edittextPetaddWeightError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                val dogName = edittextPetaddName.text.toString()
                val dogType = edittextPetaddSelectType.text.toString()
                val dogGender = getCheckedGender(binding.root, chipgroupPetaddGroupGender.checkedChipId)
                val dogBirth = dogAddViewModel.dogBirthDate.value!!
                val dogWeight: Double = edittextPetaddWeight.text.toString().toDouble()
                val dogBack: Double? = bodySizeCheck(edittextPetaddBackLength.text.toString())
                val dogNeck: Double? = bodySizeCheck(edittextPetaddNeckCircumference.text.toString())
                val dogChest: Double? = bodySizeCheck(edittextPetaddChestCircumference.text.toString())
                val dog =
                    Dog(
                        dogName,
                        dogType,
                        dogGender,
                        dogBirth,
                        isCbxChecked,
                        dogWeight,
                        dogBack,
                        dogNeck,
                        dogChest,
                    )
                val json = Gson().toJson(dog)
                val requestBody: RequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val filePart = createMultipartBody(requireContext(), dogAddViewModel.dogProfileImage.value)

                lifecycleScope.launch(postDogCoroutineJob) {
                    userPreferences.accessToken.collectLatest { accessToken ->
                        if (accessToken != null) {
                            dogAddViewModel.postDogInfo(
                                accessToken,
                                filePart,
                                requestBody,
                            )
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        dogAddCoroutineJob.cancel()
    }

    override fun onUriPassed(uri: Uri) {
        dogAddViewModel.getDogProfileImage(uri)
    }

    override fun onDateSubmit(str: String) {
        dogAddViewModel.getDogBirthDate(str)
    }
}

enum class Gender(val korean: String, val english: String) {
    MALE("남성", "male"),
    FEMALE("여성", "female"),
}

fun dateFormat(str: String): String {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")

    val parsedDate = inputDateFormat.parse(str)
    return outputDateFormat.format(parsedDate)
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

fun getCheckedGender(
    view: View,
    checkedChipId: Int,
): String {
    val checkedChip = view.findViewById<Chip>(checkedChipId)
    return if (checkedChip.text.toString() == Gender.FEMALE.korean) Gender.FEMALE.english else Gender.MALE.english
}

fun bodySizeCheck(str: String): Double? {
    return if (str.isEmpty()) null else str.toDouble()
}
