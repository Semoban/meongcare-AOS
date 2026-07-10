package com.project.meongcare.info.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSettingBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: ProfileViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val pushAgreementLiveData = MutableLiveData(false)
    private var currentAccessToken = ""
    private var currentRefreshToken = ""
    private var currentProvider = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pushAgreementLiveData.value = arguments?.getBoolean("pushAgreement") ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchUserInfo()
        observeSettingResponses()
        observeReissueResponse()
    }

    private fun initComposeView() {
        binding.composeViewSetting.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val pushAgreement by pushAgreementLiveData.observeAsState()

                    SettingScreen(
                        pushAgreement = pushAgreement == true,
                        onBackClick = { findNavController().popBackStack() },
                        onPushToggle = { isChecked ->
                            pushAgreementLiveData.value = isChecked
                            settingViewModel.patchPushAgreement(isChecked, currentAccessToken)
                        },
                        onDeleteAccountConfirm = ::deleteAccount,
                    )
                }
            }
        }
    }

    private fun fetchUserInfo() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                currentAccessToken = accessToken
            }
        }
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                currentRefreshToken = refreshToken
            }
        }
        userViewModel.providerPreferencesLiveData.observe(viewLifecycleOwner) { provider ->
            if (provider != null) {
                currentProvider = provider
            }
        }
    }

    private fun observeSettingResponses() {
        settingViewModel.userDeleteResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                200 -> {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_success_16dp,
                        getString(R.string.snack_bar_user_delete_complete),
                    ).show()
                    userViewModel.setProvider(null)
                    userViewModel.setEmail(null)
                    userViewModel.setAccessToken(null)
                    userViewModel.setRefreshToken(null)
                    findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
                }
                401 -> {
                    if (currentRefreshToken.isNotEmpty()) {
                        userViewModel.getNewAccessToken(currentRefreshToken)
                    }
                }
            }
        }

        settingViewModel.patchPushResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val messageRes =
                    if (pushAgreementLiveData.value == true) {
                        R.string.snack_bar_notification_on
                    } else {
                        R.string.snack_bar_notification_off
                    }
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    getString(messageRes),
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

    private fun observeReissueResponse() {
        userViewModel.reissueResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response.code()) {
                    200 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_user_delete_failure),
                        ).show()
                        userViewModel.setAccessToken(response.body()?.accessToken)
                    }
                    401 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_refresh_expire),
                        ).show()
                        findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
                    }
                }
            }
        }
    }

    private fun deleteAccount() {
        when (currentProvider) {
            "kakao" -> deleteKakaoAccount()
            "naver" -> deleteNaverAccount()
            "google" -> deleteGoogleAccount()
        }
    }

    private fun deleteKakaoAccount() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("Delete-kakao", "연결 끊기 실패", error)
            } else {
                Log.d("Delete-kakao", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                settingViewModel.deleteUser(currentAccessToken)
            }
        }
    }

    private fun deleteNaverAccount() {
        NidOAuthLogin().callDeleteTokenApi(
            object : OAuthLoginCallback {
                override fun onError(
                    errorCode: Int,
                    message: String,
                ) {
                    onFailure(errorCode, message)
                }

                override fun onFailure(
                    httpStatus: Int,
                    message: String,
                ) {
                    Log.e("Delete-naver", "토큰 삭제 실패 : ${NaverIdLoginSDK.getLastErrorDescription()}")
                }

                override fun onSuccess() {
                    Log.d("Delete-naver", "토큰 삭제 성공, 연동 해제 됨")
                    settingViewModel.deleteUser(currentAccessToken)
                }
            },
        )
    }

    private fun deleteGoogleAccount() {
        val gso =
            GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN,
            ).build()
        val googleSignInClient =
            this.let {
                GoogleSignIn.getClient(requireContext(), gso)
            }

        googleSignInClient.revokeAccess()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Delete-google", "회원 탈퇴 성공")
                    settingViewModel.deleteUser(currentAccessToken)
                } else {
                    Log.e("Delete-google", "회원 탈퇴 실패 ${task.result}")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
