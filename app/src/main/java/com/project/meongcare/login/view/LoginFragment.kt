package com.project.meongcare.login.view

import android.app.Instrumentation.ActivityResult
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.project.meongcare.BuildConfig
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentLoginBinding
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.entities.LoginRequest
import com.project.meongcare.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        getGoogleResult(task)
    }
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        mainActivity.detachBottomNav()

        loginViewModel.loginResponse.observe(viewLifecycleOwner){ loginResponse ->
            if(loginResponse != null){
                Log.d("Login-viewmodel", "통신 성공 후 액세스 토큰 반환 ${loginResponse.accessToken}")
                userPreferences.setAccessToken(loginResponse.accessToken)
                userPreferences.setRefreshToken(loginResponse.refreshToken)
                // 강아지 등록 화면으로 이동
                mainActivity.replaceFragment(MainActivity.DOG_ADD_ON_BOARDING_FRAGMENT, true,true, null)
            }
            else{
                Log.d("Login-viewmodel", "통신 실패")
            }
        }

        fragmentLoginBinding.run {
            buttonKakaoLogin.setOnClickListener {
                kakaoLogin()
            }
            buttonNaverLogin.setOnClickListener {
                naverLogin()
            }
            buttonGoogleLogin.setOnClickListener {
                googleLogin()
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

                // data store에 저장
                userPreferences.setEmail(user.kakaoAccount?.email!!)

                val loginRequest = LoginRequest( "${user.id}", "kakao",
                    "김멍멍", "${user.kakaoAccount?.email}", "${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                loginViewModel.postLoginInfo(loginRequest)
            }
        }
    }

    private fun naverLogin(){
        val nidProfileCallback = object: NidProfileCallback<NidProfileResponse> {
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Log.d("Login-naver", "errorCode:${errorCode}, errorDescription:${errorDescription}")
            }

            override fun onSuccess(result: NidProfileResponse) {
                if(result.profile != null){
                    Log.d("Login-naver", "프로필 가져오기 성공 ${result.profile?.profileImage}")

                    // data store에 저장
                    userPreferences.setEmail(result.profile?.email!!)

                    // 서버에 로그인 정보 전송
                    val loginRequest = LoginRequest("${result.profile?.id}", "naver",
                    "김멍멍", "${result.profile?.email}", "${result.profile?.profileImage}")
                    loginViewModel.postLoginInfo(loginRequest)
                }
            }
        }

        val oauthLoginCallback = object: OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Log.e("Login-naver", "errorCode:${errorCode}, errorDescription:${errorDescription}")
            }

            override fun onSuccess() {
                Log.d("Login-naver", "로그인 성공")
                val accessToken = NaverIdLoginSDK.getAccessToken()
                val refreshToken = NaverIdLoginSDK.getRefreshToken()
                NidOAuthLogin().callProfileApi(nidProfileCallback)
            }
        }
        NaverIdLoginSDK.authenticate(mainActivity, oauthLoginCallback)
    }

    private fun googleLogin(){
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    private fun getGoogleClient(): GoogleSignInClient{
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .build()

        return GoogleSignIn.getClient(mainActivity, googleSignInOptions)
    }

    private fun getGoogleResult(task: Task<GoogleSignInAccount>){
        try {
            val account = task.getResult(ApiException::class.java)

            // data store에 저장
            userPreferences.setEmail(account.email!!)

            val loginRequest = LoginRequest( "${account.idToken}", "google",
                "김멍멍", "${account.email}", "${account.photoUrl}")
            loginViewModel.postLoginInfo(loginRequest)
        } catch (e: ApiException){
            Log.e("Login-google", e.stackTraceToString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.attachBottomNav()
    }
}
