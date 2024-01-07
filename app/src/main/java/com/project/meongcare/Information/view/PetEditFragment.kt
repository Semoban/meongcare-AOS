package com.project.meongcare.Information.view

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.Information.viewmodel.ProfileViewModel
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentPetEditBinding
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.model.entities.Dog
import com.project.meongcare.onboarding.view.Gender
import com.project.meongcare.onboarding.view.PhotoSelectBottomSheetFragment
import com.project.meongcare.onboarding.view.bodySizeCheck
import com.project.meongcare.onboarding.view.createMultipartBody
import com.project.meongcare.onboarding.view.dateFormat
import com.project.meongcare.onboarding.view.getCheckedGender
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class PetEditFragment : Fragment(), PhotoMenuListener, DateSubmitListener {
    private lateinit var binding: FragmentPetEditBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dogInfo: GetDogInfoResponse

    private val petEditViewModel: ProfileViewModel by viewModels()
    private var isCbxChecked = false
    private var isInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPetEditBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        dogInfo = getDogInfo()

        if (!isInitialized) {
            initDogInfo(dogInfo)
            isInitialized = true
        }

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
            if (response != null && response == 200) {
                findNavController().popBackStack()
            }
        }

        binding.run {
            editTextWatcher(edittextPeteditName, edittextPeteditName, "이름을 입력해주세요")
            editTextWatcher(edittextPeteditType, edittextPeteditType, "품종을 입력해주세요")
            editTextWatcher(edittextPeteditSelectBirthday, edittextPeteditSelectBirthday, "날짜를 선택해주세요")
            editTextWatcher(edittextPeteditWeight, viewPeteditWeight, "")

            cardviewPeteditImage.setOnClickListener {
                val modalBottomSheet = PhotoSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@PetEditFragment)
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            edittextPeteditType.setOnClickListener {
                findNavController().navigate(R.id.action_petEditFragment_to_dogVarietySearchFragment)
            }

            checkboxPeteditNeuterStatus.setOnCheckedChangeListener { buttonView, isChecked ->
                isCbxChecked = isChecked
            }

            textviewPeteditNeuterStatus.setOnClickListener {
                checkboxPeteditNeuterStatus.isChecked = !isCbxChecked
            }

            imageviewPeteditBirthdayCalender.setOnClickListener {
                val calendarBottomSheet = CalendarBottomSheetFragment()
                calendarBottomSheet.setDateSubmitListener(this@PetEditFragment)
                calendarBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
                calendarBottomSheet.show(mainActivity.supportFragmentManager, calendarBottomSheet.tag)
            }

            buttonPeteditCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonPeteditSubmit.setOnClickListener {
                if (edittextPeteditName.text.isEmpty()) {
                    return@setOnClickListener
                }
                if (edittextPeteditType.text.isEmpty()) {
                    return@setOnClickListener
                }
                if (edittextPeteditSelectBirthday.text.isEmpty()) {
                    return@setOnClickListener
                }
                if (edittextPeteditWeight.text.isEmpty()) {
                    return@setOnClickListener
                }

                val dogName = edittextPeteditName.text.toString()
                val dogType = edittextPeteditType.text.toString()
                val dogGender = getCheckedGender(binding.root, chipgroupPeteditGroupGender.checkedChipId)
                val dogBirth = petEditViewModel.dogBirth.value!!
                val dogWeight = edittextPeteditWeight.text.toString().toDouble()
                val dogBack = bodySizeCheck(edittextPeteditBackLength.text.toString())
                val dogNeck = bodySizeCheck(edittextPeteditNeckCircumference.text.toString())
                val dogChest = bodySizeCheck(edittextPeteditChestCircumference.text.toString())
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
                val filePart = createMultipartBody(mainActivity, petEditViewModel.dogProfile.value)

                val accessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZXhwIjoxNzA0NjI4MzQzfQ._y66Fy6QfznE14qRncC0kPaEVHErorVRwW4zAhoW2hI"
                petEditViewModel.putDogInfo(dogInfo.dogId, accessToken, filePart, requestBody)
            }
        }

        return binding.root
    }

    private fun editTextWatcher(editText: EditText, targetView: View, hint: String) {
        editText.addTextChangedListener {
            editText.doAfterTextChanged { editable ->
                updateEditTextStyle(editText, targetView, hint)
            }
        }
    }

    private fun updateEditTextStyle(editText: EditText, targetView: View, hint: String) {
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
            edittextPeteditType.setText(dogInfo.type)
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
    }

    override fun onDateSubmit(str: String) {
        petEditViewModel.setDogBirth(str)
    }
}
