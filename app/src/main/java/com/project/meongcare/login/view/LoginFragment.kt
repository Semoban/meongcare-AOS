package com.project.meongcare.login.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentLoginBinding
import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        mainActivity.detachBottomNav()

        loginViewModel.loginResponse.observe(viewLifecycleOwner){
            Log.d("Login-kakao", "${it.accessToken}")
        }

        fragmentLoginBinding.run {
            buttonKakaoLogin.setOnClickListener {
                kakaoLogin()
            }
        }

        return fragmentLoginBinding.root
    }

    private fun kakaoLogin(){
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null){
                Log.e("Login-kakao", "카카오계정으로 로그인 실패", error)
            } else if (token != null){
                Log.d("Login-kakao", "카카오계정으로 로그인 성공 ${token.accessToken}")
                getKakaoLoginInfo()
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                if (error != null) {
                    Log.e("Login-kakao", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                } else if (token != null) {
                    Log.i("Login-kakao", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    getKakaoLoginInfo()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
        }
    }

    fun getKakaoLoginInfo(){
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("Login-kakao", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.d("Login-kakao", "사용자 정보 요청 성공" )
                val loginRequest = LoginRequest( "${user.id}", "kakao",
                    "김멍멍", "${user.kakaoAccount?.email}", "${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                loginViewModel.postLoginInfo(loginRequest)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.attachBottomNav()
    }
}
