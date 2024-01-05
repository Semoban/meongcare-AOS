package com.project.meongcare.onboarding.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.model.data.repository.DogAddRepository
import com.project.meongcare.onboarding.model.entities.Dog
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
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
    lateinit var fragmentDogAddOnBoardingBinding: FragmentDogAddOnBoardingBinding
    lateinit var mainActivity: MainActivity

    private val dogAddViewModel: DogAddViewModel by viewModels()

    @Inject
    lateinit var dogAddRepository: DogAddRepository

    @Inject
    lateinit var userPreferences: UserPreferences

    private var isCbxChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentDogAddOnBoardingBinding = FragmentDogAddOnBoardingBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        dogAddViewModel.dogBirthDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                fragmentDogAddOnBoardingBinding.textviewPetaddSelectBirthday.run {
                    fragmentDogAddOnBoardingBinding.edittextPetaddSelectBirthdayError.visibility = View.INVISIBLE
                    text = dateFormat(date)
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }
        }
        // 품종 뷰모델 옵저버 내에서 에러뷰 visibility 설정 필

        fragmentDogAddOnBoardingBinding.run {
            // 품종 검색 화면 연결 전 임시 값 설정
            edittextPetaddSelectType.text = "말티즈"

            // 사진 등록
            cardviewPetaddImage.setOnClickListener {
                val modalBottomSheet = PhotoSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@DogAddOnBoardingFragment)
                // 둥근 모서리 지정
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            // 품종 등록
            viewPetaddType.setOnClickListener {
                // 품종 검색 화면으로 이동
            }

            // 날짜 등록
            imageviewPetaddBirthdayCalender.setOnClickListener {
                val calendarBottomSheet = CalendarBottomSheetFragment()
                calendarBottomSheet.setDateSubmitListener(this@DogAddOnBoardingFragment)
                calendarBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
                calendarBottomSheet.show(mainActivity.supportFragmentManager, calendarBottomSheet.tag)
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
                // 품종 검색 화면으로 이동
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
                val dogGender = getCheckedGender(chipgroupPetaddGroupGender.checkedChipId)
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
                val filePart = createMultipartBody(mainActivity, dogAddViewModel.dogProfileImage.value)

                // 서버로 전송
                runBlocking {
                    val dogAddResponse =
                        dogAddRepository.postDogInfo(
                            "",
                            filePart,
                            requestBody,
                        )
                    if (dogAddResponse == 200) {
                        // CompleteOnBoardingFragment로 이동
                    }
                }
            }
        }

        return fragmentDogAddOnBoardingBinding.root
    }

    override fun onUriPassed(uri: Uri) {
        dogAddViewModel.getDogProfileImage(uri)

        fragmentDogAddOnBoardingBinding.run {
            Glide.with(this@DogAddOnBoardingFragment)
                .load(uri)
                .into(imageviewPetaddImage)
            imageviewPetaddDog.visibility = View.GONE
            textviewPetaddImageDescription.visibility = View.GONE
        }
    }

    override fun onDateSubmit(str: String) {
        dogAddViewModel.getDogBirthDate(str)
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

    fun getCheckedGender(checkedChipId: Int): String {
        val checkedChip = fragmentDogAddOnBoardingBinding.root.findViewById<Chip>(checkedChipId)
        return if (checkedChip.text.toString() == Gender.FEMALE.korean) Gender.FEMALE.english else Gender.MALE.english
    }

    fun bodySizeCheck(str: String): Double? {
        return if (str.isEmpty()) null else str.toDouble()
    }
}

enum class Gender(val korean: String, val english: String) {
    MALE("남성", "male"),
    FEMALE("여성", "female"),
}
