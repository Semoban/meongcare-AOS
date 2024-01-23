package com.project.meongcare.login.view

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.project.meongcare.BuildConfig
import dagger.hilt.android.HiltAndroidApp

// data store 인스턴스 생성
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "userPreferences")
val Context.dogDataStore: DataStore<Preferences> by preferencesDataStore(name = "dogPreferences")

@HiltAndroidApp
class GlobalApplication : Application() {
    lateinit var context: Context

    init {
        instance = this
    }

    companion object {
        private var instance: GlobalApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        // kakao sdk 초기화
        KakaoSdk.init(this, "${BuildConfig.KAKAO_NATIVE_APP_KEY}")

        // naver sdk 초기화
        NaverIdLoginSDK.initialize(
            this,
            "${BuildConfig.NAVER_OAUTH_CLIENT_ID}",
            "${BuildConfig.NAVER_OAUTH_CLIENT_SECRET}",
            "${BuildConfig.NAVER_OAUTH_CLIENT_NAME}",
        )
    }
}
