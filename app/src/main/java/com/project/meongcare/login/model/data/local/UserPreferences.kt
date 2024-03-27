package com.project.meongcare.login.model.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.meongcare.login.view.userDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserPreferences
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        // 키 정의
        private val preferenceKeyProvider = stringPreferencesKey("provider")
        private val preferenceKeyEmail = stringPreferencesKey("email")
        private val preferenceKeyAccessToken = stringPreferencesKey("accessToken")
        private val preferenceKeyRefreshToken = stringPreferencesKey("refreshToken")
        private val preferenceKeyIsFirstLogin = booleanPreferencesKey("isFirstLogin")

        // 값 저장(수정)
        suspend fun editProvider(provider: String?) {
            context.userDataStore.edit { preferences ->
                if (provider == null) {
                    preferences.remove(preferenceKeyProvider)
                } else {
                    preferences[preferenceKeyProvider] = provider
                }
            }
        }

        suspend fun editEmail(email: String?) {
            context.userDataStore.edit { preferences ->
                if (email == null) {
                    preferences.remove(preferenceKeyEmail)
                } else {
                    preferences[preferenceKeyEmail] = email
                }
            }
        }

        suspend fun editAccessToken(accessToken: String?) {
            context.userDataStore.edit { preferences ->
                if (accessToken == null) {
                    preferences.remove(preferenceKeyAccessToken)
                } else {
                    preferences[preferenceKeyAccessToken] = accessToken
                }
            }
        }

        suspend fun editRefreshToken(refreshToken: String?) {
            context.userDataStore.edit { preferences ->
                if (refreshToken == null) {
                    preferences.remove(preferenceKeyRefreshToken)
                } else {
                    preferences[preferenceKeyRefreshToken] = refreshToken
                }
            }
        }

        suspend fun editIsFirstLogin(isFirstLogin: Boolean?) {
            context.userDataStore.edit { preferences ->
                if (isFirstLogin == null) {
                    preferences.remove(preferenceKeyIsFirstLogin)
                } else {
                    preferences[preferenceKeyIsFirstLogin] = isFirstLogin
                }
            }
        }

        fun setProvider(provider: String?) {
            CoroutineScope(Dispatchers.IO).launch {
                editProvider(provider)
            }
        }

        fun setEmail(email: String?) {
            CoroutineScope(Dispatchers.IO).launch {
                editEmail(email)
            }
        }

        fun setAccessToken(accessToken: String?) {
            CoroutineScope(Dispatchers.IO).launch {
                editAccessToken(accessToken)
            }
        }

        fun setRefreshToken(refreshToken: String?) {
            CoroutineScope(Dispatchers.IO).launch {
                editRefreshToken(refreshToken)
            }
        }

        fun setIsFirstLogin(isFirstLogin: Boolean?) {
            CoroutineScope(Dispatchers.IO).launch {
                editIsFirstLogin(isFirstLogin)
            }
        }

        // 값 가져오기
        val provider: Flow<String?> =
            context.userDataStore.data.map { preferences ->
                preferences[preferenceKeyProvider]
            }

        val email: Flow<String?> =
            context.userDataStore.data.map { preferences ->
                preferences[preferenceKeyEmail]
            }

        val accessToken: Flow<String?> =
            context.userDataStore.data.map { preferences ->
                preferences[preferenceKeyAccessToken]
            }

        val refreshToken: Flow<String?> =
            context.userDataStore.data.map { preferences ->
                preferences[preferenceKeyRefreshToken]
            }

        // first() : 저장된 데이터 중 가장 최신 시점의 데이터를 반환
        suspend fun getAccessToken(): String {
            return context.userDataStore.data.first()[preferenceKeyAccessToken] ?: ""
        }

        suspend fun getRefreshToken(): String {
            return context.userDataStore.data.first()[preferenceKeyRefreshToken] ?: ""
        }

        suspend fun getProvider(): String {
            return context.userDataStore.data.first()[preferenceKeyProvider] ?: ""
        }

        suspend fun getIsFirstLogin(): Boolean? {
            return context.userDataStore.data.first()[preferenceKeyIsFirstLogin]
        }
    }
