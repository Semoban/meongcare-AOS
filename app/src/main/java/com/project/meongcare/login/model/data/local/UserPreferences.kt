package com.project.meongcare.login.model.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.meongcare.login.view.dataStore
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
        @ApplicationContext private val context: Context
    ) {
        // 키 정의
        private val preferenceKeyEmail = stringPreferencesKey("email")
        private val preferenceKeyAccessToken = stringPreferencesKey("accessToken")
        private val preferenceKeyRefreshToken = stringPreferencesKey("refreshToken")

        // 값 저장(수정)
        private suspend fun editEmail(email: String) {
            context.dataStore.edit { preferences ->
                preferences[preferenceKeyEmail] = email
            }
        }

        private suspend fun editAccessToken(accessToken: String) {
            context.dataStore.edit { preferences ->
                preferences[preferenceKeyAccessToken] = accessToken
            }
        }

        private suspend fun editRefreshToken(refreshToken: String) {
            context.dataStore.edit { preferences ->
                preferences[preferenceKeyRefreshToken] = refreshToken
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
        val email: Flow<String?> =
            context.dataStore.data.map { preferences ->
                preferences[preferenceKeyEmail]
            }

        val accessToken: Flow<String?> =
            context.dataStore.data.map { preferences ->
                preferences[preferenceKeyAccessToken]
            }

        val refreshToken: Flow<String?> =
            context.dataStore.data.map { preferences ->
                preferences[preferenceKeyRefreshToken]
            }
    }
