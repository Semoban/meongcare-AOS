package com.project.meongcare.login.model.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.meongcare.login.view.userDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

        // 값 저장(수정)
        private suspend fun editProvider(provider: String?) {
            context.userDataStore.edit { preferences ->
                if (provider == null) {
                    preferences.remove(preferenceKeyProvider)
                } else {
                    preferences[preferenceKeyProvider] = provider
                }
            }
        }

        private suspend fun editEmail(email: String) {
            context.userDataStore.edit { preferences ->
                preferences[preferenceKeyEmail] = email
            }
        }

        private suspend fun editAccessToken(accessToken: String) {
            context.userDataStore.edit { preferences ->
                preferences[preferenceKeyAccessToken] = accessToken
            }
        }

        private suspend fun editRefreshToken(refreshToken: String) {
            context.userDataStore.edit { preferences ->
                preferences[preferenceKeyRefreshToken] = refreshToken
            }
        }

        fun setProvider(provider: String?) {
            CoroutineScope(Dispatchers.IO).launch {
                editProvider(provider)
            }
        }

        fun setEmail(email: String) {
            CoroutineScope(Dispatchers.IO).launch {
                editEmail(email)
            }
        }

        fun setAccessToken(accessToken: String) {
            CoroutineScope(Dispatchers.IO).launch {
                editAccessToken(accessToken)
            }
        }

        fun setRefreshToken(refreshToken: String) {
            CoroutineScope(Dispatchers.IO).launch {
                editRefreshToken(refreshToken)
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
    }
