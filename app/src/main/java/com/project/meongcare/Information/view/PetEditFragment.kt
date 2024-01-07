package com.project.meongcare.Information.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.Information.viewmodel.ProfileViewModel
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentPetEditBinding
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.view.Gender
import com.project.meongcare.onboarding.view.PhotoSelectBottomSheetFragment
import com.project.meongcare.onboarding.view.dateFormat
import dagger.hilt.android.AndroidEntryPoint

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

        binding.run {
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
        }

        return binding.root
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