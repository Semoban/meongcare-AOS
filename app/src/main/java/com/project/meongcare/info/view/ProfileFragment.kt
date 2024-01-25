package com.project.meongcare.info.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentProfileBinding
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.view.createMultipartBody
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), PhotoMenuListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainActivity: MainActivity

    private val profileViewModel: ProfileViewModel by viewModels()
    private val logoutCoroutineJob = Job()
    private lateinit var profileUri: Uri
    private lateinit var currentAccessToken: String

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getAccessToken()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        profileViewModel.getUserProfile(currentAccessToken)
        profileViewModel.getDogList(currentAccessToken)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse != null) {
                when (profileResponse.code()) {
                    200 -> {
                        binding.run {
                            Glide.with(this@ProfileFragment)
                                .load(profileResponse.body()?.imageUrl)
                                .error(R.drawable.profile_default_image)
                                .into(imageviewProfileImage)
                            textviewProfileEmail.text = profileResponse.body()?.email
                        }
                    }
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
                                            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_get_profile_failure),
                        ).show()
                    }
                }
            }
        }

        profileViewModel.dogList.observe(viewLifecycleOwner) { dogListResponse ->
            if (dogListResponse != null && dogListResponse.body() != null) {
                when (dogListResponse.code()) {
                    200 -> {
                        binding.textViewNoDog.visibility = View.GONE
                        binding.recyclerviewProfilePetList.visibility = View.VISIBLE
                        val adapter = binding.recyclerviewProfilePetList.adapter as ProfileDogAdapter
                        adapter.updateDogList(dogListResponse.body()?.dogs!!)
                    }
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
                                            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_get_dog_list_failure),
                        ).show()
                    }
                }
                if (dogListResponse.body()?.dogs.isNullOrEmpty()) {
                    binding.textViewNoDog.visibility = View.VISIBLE
                    binding.recyclerviewProfilePetList.visibility = View.GONE
                }
            }
        }

        profileViewModel.logoutResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                lifecycleScope.launch(logoutCoroutineJob) {
                    userPreferences.provider.collect { provider ->
                        when (provider) {
                            "kakao" -> kakaoLogout()
                            "naver" -> naverLogout()
                            "google" -> googleLogout()
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

        profileViewModel.patchProfileResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                binding.run {
                    Glide.with(this@ProfileFragment)
                        .load(profileUri)
                        .into(imageviewProfileImage)
                }
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    getString(R.string.snack_bar_profile_update_complete),
                ).show()
            } else {
                binding.run {
                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.profile_default_image)
                        .into(imageviewProfileImage)
                }
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }

        binding.run {
            imagebuttonProfileBack.setOnClickListener {
                findNavController().popBackStack()
            }

            imageviewProfileImage.setOnClickListener {
                val modalBottomSheet = UserProfileSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@ProfileFragment)
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            recyclerviewProfilePetList.run {
                adapter = ProfileDogAdapter(layoutInflater, context)
                layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
            }

            buttonProfileSetting.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean("pushAgreement", profileViewModel.userProfile.value?.body()?.pushAgreement!!)
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment, bundle)
            }

            buttonProfileLogout.setOnClickListener {
                includeLogoutDialog.root.visibility = View.VISIBLE
            }
            includeLogoutDialog.run {
                constraintlayoutBg.setOnClickListener {
                    includeLogoutDialog.root.visibility = View.GONE
                }
                cardviewDialog.setOnClickListener {
                    includeLogoutDialog.root.visibility = View.VISIBLE
                }
                buttonLogoutDialogCancel.setOnClickListener {
                    includeLogoutDialog.root.visibility = View.GONE
                }
                buttonLogoutDialogLogout.setOnClickListener {
                    lifecycleScope.launch(logoutCoroutineJob) {
                        val refreshToken = userPreferences.getRefreshToken()
                        if (refreshToken != null) {
                            profileViewModel.logoutUser(refreshToken)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logoutCoroutineJob.cancel()
    }

    override fun onUriPassed(uri: Uri) {
        profileUri = uri
        val multipartBody = createMultipartBody(requireContext(), uri)
        profileViewModel.patchProfileImage(currentAccessToken, multipartBody)
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

    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("Logout-kakao", "로그아웃 실패, SDK에서 토큰 삭제됨\n $error")
            } else {
                Log.i("Logout-kakao", "로그아웃 성공, SDK에서 토큰 삭제됨")
                userPreferences.setProvider(null)
                userPreferences.setAccessToken(null)
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }
    }

    private fun naverLogout() {
        NaverIdLoginSDK.logout()
        userPreferences.setProvider(null)
        userPreferences.setAccessToken(null)
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    private fun googleLogout() {
        val gso =
            GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN,
            ).build()
        val googleSignInClient =
            this.let {
                GoogleSignIn.getClient(requireContext(), gso)
            }

        googleSignInClient.signOut()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Logout-google", "로그아웃 성공")
                    userPreferences.setProvider(null)
                    userPreferences.setAccessToken(null)
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                } else {
                    Log.e("Logout-google", "로그아웃 실패")
                }
            }
    }
}
