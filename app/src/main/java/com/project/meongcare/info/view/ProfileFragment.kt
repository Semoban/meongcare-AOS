package com.project.meongcare.info.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.project.meongcare.BuildConfig
import com.project.meongcare.PhotoSelectBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.MEMBER_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentProfileBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.info.model.entities.ProfilePatchRequest
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment(), PhotoMenuListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageFile: File
    private lateinit var filePath: String

    private val profileViewModel: ProfileViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var currentAccessToken = ""
    private var currentRefreshToken = ""
    private var currentProvider = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchUserInfo()
        observeProfileResponses()
        observeImageUpload()
        observeReissueResponse()
    }

    private fun initComposeView() {
        binding.composeViewProfile.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val email by userViewModel.emailPreferencesLiveData.observeAsState()
                    val userProfile by profileViewModel.userProfile.observeAsState()
                    val dogList by profileViewModel.dogList.observeAsState()

                    ProfileScreen(
                        email = email,
                        profileImageUrl = userProfile?.body()?.imageUrl,
                        dogs = dogList?.body()?.dogs.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                        onProfileImageClick = ::showPhotoSelectBottomSheet,
                        onDogClick = ::navigateToPetInfo,
                        onSettingClick = ::navigateToSetting,
                        onLogoutConfirm = { profileViewModel.logoutUser(currentRefreshToken) },
                    )
                }
            }
        }
    }

    private fun fetchUserInfo() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                currentAccessToken = accessToken
                fetchProfileIfReady()
            }
        }
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                currentRefreshToken = refreshToken
                fetchProfileIfReady()
            }
        }
        userViewModel.providerPreferencesLiveData.observe(viewLifecycleOwner) { provider ->
            if (provider != null) {
                currentProvider = provider
            }
        }
    }

    // 토큰이 모두 준비된 시점에만 프로필·반려견 목록을 조회한다
    private fun fetchProfileIfReady() {
        if (currentAccessToken.isEmpty() || currentRefreshToken.isEmpty()) return

        profileViewModel.getUserProfile(currentAccessToken)
        profileViewModel.getDogList(currentAccessToken)
    }

    private fun observeProfileResponses() {
        profileViewModel.userProfile.observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse != null) {
                when (profileResponse.code()) {
                    200 -> {}
                    401 -> {
                        if (currentRefreshToken.isNotEmpty()) {
                            userViewModel.getNewAccessToken(currentRefreshToken)
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
                    200 -> {}
                    401 -> {
                        if (currentRefreshToken.isNotEmpty()) {
                            userViewModel.getNewAccessToken(currentRefreshToken)
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
            }
        }

        profileViewModel.logoutResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (currentProvider) {
                    "kakao" -> kakaoLogout()
                    "naver" -> naverLogout()
                    "google" -> googleLogout()
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
                profileViewModel.getUserProfile(currentAccessToken)
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    getString(R.string.snack_bar_profile_update_complete),
                ).show()
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }
    }

    private fun observeImageUpload() {
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val profilePatchRequest = ProfilePatchRequest(BuildConfig.AWS_S3_BASE_URL + filePath)
                profileViewModel.patchProfileImage(currentAccessToken, profilePatchRequest)
            }
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
                        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                    }
                }
            }
        }
    }

    override fun onUriPassed(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$MEMBER_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(currentAccessToken, filePath)
    }

    private fun uploadImage(
        preSignedURL: String,
        requestBody: RequestBody,
    ) {
        awsS3ViewModel.uploadImageToS3(preSignedURL, requestBody)
    }

    private fun showPhotoSelectBottomSheet() {
        val modalBottomSheet = PhotoSelectBottomSheetFragment()
        modalBottomSheet.setPhotoMenuListener(this@ProfileFragment)
        modalBottomSheet.setDefaultImageRes(R.drawable.profile_default_image)
        modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
        modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
    }

    private fun navigateToPetInfo(dogId: Long) {
        val bundle = Bundle()
        bundle.putLong("dogId", dogId)
        findNavController().navigate(R.id.action_profileFragment_to_petAddFragment, bundle)
    }

    private fun navigateToSetting() {
        val bundle = Bundle()
        bundle.putBoolean("pushAgreement", profileViewModel.userProfile.value?.body()?.pushAgreement!!)
        findNavController().navigate(R.id.action_profileFragment_to_settingFragment, bundle)
    }

    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("Logout-kakao", "로그아웃 실패, SDK에서 토큰 삭제됨\n $error")
            } else {
                Log.i("Logout-kakao", "로그아웃 성공, SDK에서 토큰 삭제됨")
                userViewModel.setProvider(null)
                userViewModel.setAccessToken(null)
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }
    }

    private fun naverLogout() {
        NaverIdLoginSDK.logout()
        userViewModel.setProvider(null)
        userViewModel.setAccessToken(null)
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
                    userViewModel.setProvider(null)
                    userViewModel.setAccessToken(null)
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                } else {
                    Log.e("Logout-google", "로그아웃 실패")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
