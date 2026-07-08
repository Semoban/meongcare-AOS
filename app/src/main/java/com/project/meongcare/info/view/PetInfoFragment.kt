package com.project.meongcare.info.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentPetAddEditBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PetInfoFragment : Fragment() {
    private var _binding: FragmentPetAddEditBinding? = null
    private val binding get() = _binding!!

    private val petInfoViewModel: ProfileViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var currentAccessToken = ""
    private var currentRefreshToken = ""
    private var dogId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dogId = arguments?.getLong("dogId")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPetAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchDogInfo()
        observeDogInfoResponse()
        observeDogDeleteResponse()
        observeReissueResponse()
    }

    private fun initComposeView() {
        binding.composeViewPetInfo.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogInfoResponse by petInfoViewModel.dogInfo.observeAsState()

                    PetInfoScreen(
                        dogInfo = dogInfoResponse?.body(),
                        onBackClick = { findNavController().popBackStack() },
                        onEditClick = ::navigateToPetEdit,
                        onDeleteConfirm = { petInfoViewModel.deleteDog(dogId, currentAccessToken) },
                    )
                }
            }
        }
    }

    private fun fetchDogInfo() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                currentAccessToken = accessToken
                petInfoViewModel.getDogInfo(dogId, accessToken)
            }
        }
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                currentRefreshToken = refreshToken
            }
        }
    }

    private fun observeDogInfoResponse() {
        petInfoViewModel.dogInfo.observe(viewLifecycleOwner) { dogInfoResponse ->
            if (dogInfoResponse != null) {
                when (dogInfoResponse.code()) {
                    200 -> {}
                    401 -> {
                        if (currentRefreshToken.isNotEmpty()) {
                            userViewModel.getNewAccessToken(currentRefreshToken)
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
    }

    private fun observeDogDeleteResponse() {
        petInfoViewModel.dogDeleteResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) findNavController().popBackStack()
        }
    }

    private fun observeReissueResponse() {
        userViewModel.reissueResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response.code()) {
                    200 -> {
                        userViewModel.setAccessToken(response.body()?.accessToken)
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

    private fun navigateToPetEdit() {
        val bundle = Bundle()
        bundle.putParcelable("dogInfo", petInfoViewModel.dogInfo.value?.body()!!)
        findNavController().navigate(R.id.action_petInfoFragment_to_petEditFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
