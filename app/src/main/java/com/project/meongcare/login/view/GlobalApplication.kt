package com.project.meongcare.login.view

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.project.meongcare.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //kakao sdk 초기화
        KakaoSdk.init(this, "${BuildConfig.KAKAO_NATIVE_APP_KEY}")

        //naver sdk 초기화
        NaverIdLoginSDK.initialize(this, "${BuildConfig.NAVER_OAUTH_CLIENT_ID}",
        "${BuildConfig.NAVER_OAUTH_CLIENT_SECRET}", "${BuildConfig.NAVER_OAUTH_CLIENT_NAME}")
    }
}