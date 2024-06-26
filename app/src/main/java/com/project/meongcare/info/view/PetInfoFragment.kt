package com.project.meongcare.info.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentPetAddEditBinding
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.onboarding.model.entities.Gender
import com.project.meongcare.onboarding.util.DogAddOnBoardingDateUtils.dateFormat
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PetInfoFragment : Fragment() {
    private lateinit var binding: FragmentPetAddEditBinding

    private val petInfoViewModel: ProfileViewModel by viewModels()
    private lateinit var currentAccessToken: String
    private var dogId: Long = 0

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dogId = arguments?.getLong("dogId")!!
        getAccessToken()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPetAddEditBinding.inflate(inflater)

        petInfoViewModel.getDogInfo(dogId, currentAccessToken)

        petInfoViewModel.dogInfo.observe(viewLifecycleOwner) { dogInfoResponse ->
            if (dogInfoResponse != null) {
                when (dogInfoResponse.code()) {
                    200 -> initDogInfo(dogInfoResponse.body()!!)
                    401 -> {
                        lifecycleScope.launch {
                            val refreshToken = userPreferences.getRefreshToken()
                            if (refreshToken.isNotEmpty()) {
                                val response = loginRepository.getNewAccessToken(refreshToken)
                                if (response != null) {
                                    when (response.code()) {
                                        200 -> {
                                            userPreferences.setAccessToken(response.body()?.accessToken!!)
                                        }
                                        401 -> {
                                            CustomSnackBar.make(
                                                requireView(),
                                                R.drawable.snackbar_error_16dp,
                                                getString(R.string.snack_bar_refresh_expire),
                                            ).show()
                                            findNavController().navigate(R.id.action_petInfoFragment_to_loginFragment)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_get_dog_failure),
                ).show()
            }
        }

        binding.run {
            imagebuttonPetaddBack.setOnClickListener {
                findNavController().popBackStack()
            }
            imagebuttonPetaddEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("dogInfo", petInfoViewModel.dogInfo.value?.body()!!)
                findNavController().navigate(R.id.action_petInfoFragment_to_petEditFragment, bundle)
            }
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
                        petInfoViewModel.deleteDog(dogId, currentAccessToken)
                        petInfoViewModel.dogDeleteResponse.observe(viewLifecycleOwner) { response ->
                            if (response == 200) findNavController().popBackStack()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun getAccessToken() {
        lifecycleScope.launch {
            userPreferences.accessToken.collectLatest { accessToken ->
                if (accessToken != null) {
                    currentAccessToken = accessToken
                }
            }
        }
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
