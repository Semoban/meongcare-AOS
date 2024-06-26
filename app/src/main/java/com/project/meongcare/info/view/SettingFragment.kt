package com.project.meongcare.info.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSettingBinding
import com.project.meongcare.databinding.LayoutDeleteAccountDialogBinding
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var currentAccessToken: String

    private val settingViewModel: ProfileViewModel by viewModels()
    private val deleteAccountCoroutineJob = Job()
    private var pushAgreement = false

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pushAgreement = arguments?.getBoolean("pushAgreement")!!
        getAccessToken()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(inflater)

        settingViewModel.userDeleteResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response) {
                    200 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_success_16dp,
                            getString(R.string.snack_bar_user_delete_complete),
                        ).show()
                        userPreferences.setProvider(null)
                        userPreferences.setEmail(null)
                        userPreferences.setAccessToken(null)
                        userPreferences.setRefreshToken(null)
                        findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
                    }
                    401 -> {
                        lifecycleScope.launch {
                            val refreshToken = userPreferences.getRefreshToken()
                            if (refreshToken.isNotEmpty()) {
                                val response = loginRepository.getNewAccessToken(refreshToken)
                                if (response != null) {
                                    when (response.code()) {
                                        200 -> {
                                            CustomSnackBar.make(
                                                requireView(),
                                                R.drawable.snackbar_error_16dp,
                                                getString(R.string.snack_bar_user_delete_failure),
                                            ).show()
                                            userPreferences.setAccessToken(response.body()?.accessToken!!)
                                        }
                                        401 -> {
                                            CustomSnackBar.make(
                                                requireView(),
                                                R.drawable.snackbar_error_16dp,
                                                getString(R.string.snack_bar_refresh_expire),
                                            ).show()
                                            findNavController().navigate(R.id.action_petEditFragment_to_loginFragment)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        settingViewModel.patchPushResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                when (binding.switchSettingNotification.isChecked) {
                    true -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_success_16dp,
                            getString(R.string.snack_bar_notification_on),
                        ).show()
                    }
                    false -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_success_16dp,
                            getString(R.string.snack_bar_notification_off),
                        ).show()
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

        binding.run {
            switchSettingNotification.run {
                isChecked = pushAgreement
            }

            imagebuttonSettingBack.setOnClickListener {
                findNavController().popBackStack()
            }

            textviewSettingMembershipWithdrawal.setOnClickListener {
                showDeleteAccountDialog()
            }

            buttonSettingNotification.setOnClickListener {
                switchSettingNotification.isChecked = !switchSettingNotification.isChecked
            }

            switchSettingNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                settingViewModel.patchPushAgreement(isChecked, currentAccessToken)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteAccountCoroutineJob.cancel()
    }

    private fun showDeleteAccountDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutDeleteAccountDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        builder.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.all_rect_white_r10))

        val dialog = builder.create()

        dialogBinding.buttonDeleteAccountDialogCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.buttonDeleteAccountDialogDelete.setOnClickListener {
            lifecycleScope.launch(deleteAccountCoroutineJob) {
                val currentProvider = userPreferences.getProvider()
                when (currentProvider) {
                    "kakao" -> deleteKakaoAccount()
                    "naver" -> deleteNaverAccount()
                    "google" -> deleteGoogleAccount()
                }
            }
        }

        dialog.show()
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
}
