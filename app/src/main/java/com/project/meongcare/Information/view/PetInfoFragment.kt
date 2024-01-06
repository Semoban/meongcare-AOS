package com.project.meongcare.Information.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.Information.model.entities.GetDogInfoResponse
import com.project.meongcare.Information.viewmodel.ProfileViewModel
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentPetAddEditBinding
import com.project.meongcare.onboarding.view.Gender
import com.project.meongcare.onboarding.view.dateFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PetInfoFragment : Fragment() {
    private lateinit var binding: FragmentPetAddEditBinding

    private val petInfoViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPetAddEditBinding.inflate(inflater)

        petInfoViewModel.dogInfo.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                initDogInfo(response)
            }
        }

        val accessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZXhwIjoxNzA0NTI5MzQxfQ.OyMJ6nYc5ts7pIik__904ClK01HFUeYxjCbITeZMLr0"
        val dogId = arguments?.getLong("dogId")!!
        petInfoViewModel.getDogInfo(dogId, accessToken)

        binding.run {
            imagebuttonPetaddDelete.setOnClickListener {
                includeDeleteDialog.root.visibility = View.VISIBLE
                includeDeleteDialog.run {
                    constraintlayoutBg.setOnClickListener {
                        includeDeleteDialog.root.visibility = View.GONE
                    }
                    cardviewDialog.setOnClickListener {
                        includeDeleteDialog.root.visibility = View.VISIBLE
                    }
                    buttonDeleteDialogCancel.setOnClickListener {
                        includeDeleteDialog.root.visibility = View.GONE
                    }
                    buttonDeleteDialogDelete.setOnClickListener {
                        petInfoViewModel.deleteDog(dogId, accessToken)
                        petInfoViewModel.dogDeleteResponse.observe(viewLifecycleOwner) { response ->
                            if (response == 200) findNavController().popBackStack()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun initDogInfo(dogInfo: GetDogInfoResponse) {
        binding.run {
            if (dogInfo.imageUrl != null) {
                Glide.with(this@PetInfoFragment)
                    .load(dogInfo.imageUrl)
                    .error(R.drawable.dog_profile_default)
                    .into(imageviewPetaddImage)
            }
            textviewPetaddName.text = dogInfo.name
            textviewPetaddType.text = dogInfo.type
            when (dogInfo.sex) {
                Gender.FEMALE.english -> chipgroupPetaddGroupGender.check(R.id.chip_petadd_female)
                Gender.MALE.english -> chipgroupPetaddGroupGender.check(R.id.chip_petadd_male)
            }
            checkboxPetaddNeuterStatus.isChecked = dogInfo.castrate
            textviewPataddSelectBirthday.text = dateFormat(dogInfo.birthDate)
            textviewPetaddWeight.text = dogInfo.weight.toString()
            if (dogInfo.backRound != null && dogInfo.backRound != 0.0) {
                textviewPetaddBackLength.text = dogInfo.backRound.toString()
            }
            if (dogInfo.neckRound != null && dogInfo.neckRound != 0.0) {
                textviewPetaddNeckCircumference.text = dogInfo.neckRound.toString()
            }
            if (dogInfo.chestRound != null && dogInfo.chestRound != 0.0) {
                textviewPetaddChestCircumference.text = dogInfo.chestRound.toString()
            }
        }
    }
}
