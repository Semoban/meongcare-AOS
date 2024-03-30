package com.project.meongcare.info.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.project.meongcare.R
import com.project.meongcare.aws.util.MEMBER_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentProfileBinding
import com.project.meongcare.databinding.LayoutLogoutDialogBinding
import com.project.meongcare.databinding.LayoutMedicalRecordDialogBinding
import com.project.meongcare.aws.util.AWSS3ImageUtils.createMultipartFromUri
import com.project.meongcare.aws.util.AWSS3ImageUtils.getMultipartFileName
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.medicalrecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

@AndroidEntryPoint
class ProfileFragment : Fragment(), PhotoMenuListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var multipartImage: MultipartBody.Part
    private lateinit var fileName: String

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
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAccessToken()
        getProvider()
        getEmail()

        profileViewModel.userProfile.observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse != null) {
                when (profileResponse.code()) {
                    200 -> {
                        binding.run {
                            Glide.with(this@ProfileFragment)
                                .load(profileResponse.body()?.imageUrl)
                                .error(R.drawable.profile_default_image)
                                .into(imageviewProfileImage)
                        }
                    }
                    401 -> {
                        lifecycleScope.launch {
                            val refreshToken = currentRefreshToken
                            if (refreshToken.isNotEmpty()) {
                                reissueAccessToken()
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
                            val refreshToken = currentRefreshToken
                            if (refreshToken.isNotEmpty()) {
                                reissueAccessToken()
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

        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                awsS3ViewModel.uploadImageToS3(response.preSignedUrl, multipartImage)
            }
        }

        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                // 저장된 이미지 경로를 데이터와 함께 서버로 전달
                //        profileViewModel.patchProfileImage(currentAccessToken, multipartBody)
            }
        }

        initPetListRecyclerView()
        initProfileImageView()
        initBackButton()
        initShareButton()
        initSettingButton()
        initLogoutButton()
    }

    override fun onUriPassed(uri: Uri) {
        multipartImage = createMultipartFromUri(requireContext(), uri)
        fileName = getMultipartFileName(multipartImage)

        val filePath = "$PARENT_FOLDER_PATH$MEMBER_FOLDER_PATH$fileName"
        awsS3ViewModel.getPreSignedUrl(currentAccessToken, filePath)
    }

    private fun reissueAccessToken() {
        userViewModel.getNewAccessToken(currentRefreshToken)
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

    private fun getAccessToken() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                currentAccessToken = accessToken
                getRefreshToken()
            }
        }
    }

    private fun getRefreshToken() {
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                currentRefreshToken = refreshToken
                profileViewModel.getUserProfile(currentAccessToken)
                profileViewModel.getDogList(currentAccessToken)
            }
        }
    }

    private fun getProvider() {
        userViewModel.providerPreferencesLiveData.observe(viewLifecycleOwner) { provider ->
            if (provider != null) {
                currentProvider = provider
            }
        }
    }

    private fun getEmail() {
        userViewModel.emailPreferencesLiveData.observe(viewLifecycleOwner) { email ->
            binding.textviewProfileEmail.text = email ?: ""
        }
    }

    private fun initPetListRecyclerView() {
        binding.recyclerviewProfilePetList.run {
            adapter = ProfileDogAdapter(layoutInflater, context)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initProfileImageView() {
        binding.imageviewProfileImage.setOnClickListener {
            val modalBottomSheet = UserProfileSelectBottomSheetFragment()
            modalBottomSheet.setPhotoMenuListener(this@ProfileFragment)
            modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
            modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
        }
    }

    private fun initBackButton() {
        binding.imagebuttonProfileBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initShareButton() {
        binding.buttonProfileShare.setOnClickListener {
            showUpdateDialog()
        }
    }

    private fun initSettingButton() {
        binding.buttonProfileSetting.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("pushAgreement", profileViewModel.userProfile.value?.body()?.pushAgreement!!)
            findNavController().navigate(R.id.action_profileFragment_to_settingFragment, bundle)
        }
    }

    private fun initLogoutButton() {
        binding.buttonProfileLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutLogoutDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        builder.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.all_rect_white_r10))

        val dialog = builder.create()

        dialogBinding.run {
            buttonLogoutDialogLogout.setOnClickListener {
                dialog.dismiss()
                profileViewModel.logoutUser(currentRefreshToken)
            }

            buttonLogoutDialogCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showUpdateDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutMedicalRecordDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        builder.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.all_rect_white_r10))

        val dialog = builder.create()

        dialogBinding.buttonOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
}
