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
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentProfileBinding
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), PhotoMenuListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainActivity: MainActivity

    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // 임시 설정
        userPreferences.setProvider("kakao")

        profileViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.run {
                    Glide.with(this@ProfileFragment)
                        .load(response.imageUrl)
                        .error(R.drawable.profile_default_image)
                        .into(imageviewProfileImage)
                    textviewProfileEmail.text = response.email
                }
            }
        }

        profileViewModel.dogList.observe(viewLifecycleOwner) { dogList ->
            if (dogList.isNotEmpty()) {
                binding.textViewNoDog.visibility = View.GONE
                binding.recyclerviewProfilePetList.visibility = View.VISIBLE
                val adapter = binding.recyclerviewProfilePetList.adapter as ProfileDogAdapter
                adapter.updateDogList(dogList)
            } else {
                binding.textViewNoDog.visibility = View.VISIBLE
                binding.recyclerviewProfilePetList.visibility = View.GONE
            }
        }

        profileViewModel.logoutResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                lifecycleScope.launch {
                    userPreferences.provider.collect { provider ->
                        when (provider) {
                            "kakao" -> kakaoLogout()
                            "naver" -> naverLogout()
                            "google" -> googleLogout()
                        }
                    }
                }
            }
        }

        val accessToken = ""
        profileViewModel.getUserProfile(accessToken)
        profileViewModel.getDogList(accessToken)

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
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
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
                    val refreshToken = ""
                    profileViewModel.logoutUser(refreshToken)
                }
            }
        }

        return binding.root
    }

    override fun onUriPassed(uri: Uri) {
        // uri로 회원 정보 patch api 연결한 후 통신 정상이면 이미지 뷰에 표시되도록 로직 변경
        binding.run {
            Glide.with(this@ProfileFragment)
                .load(uri)
                .error(R.drawable.profile_default_image)
                .into(imageviewProfileImage)
        }
    }

    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("Logout-kakao", "로그아웃 실패, SDK에서 토큰 삭제됨\n $error")
            } else {
                Log.i("Logout-kakao", "로그아웃 성공, SDK에서 토큰 삭제됨")
                userPreferences.setProvider(null)
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }
    }

    private fun naverLogout() {
        NaverIdLoginSDK.logout()
        userPreferences.setProvider(null)
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
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                } else {
                    Log.e("Logout-google", "로그아웃 실패")
                }
            }
    }
}
