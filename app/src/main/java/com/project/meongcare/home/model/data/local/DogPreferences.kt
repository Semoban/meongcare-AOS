package com.project.meongcare.home.model.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.meongcare.login.view.dogDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DogPreferences
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val preferenceDogId = longPreferencesKey("dogId")
        private val preferenceDogName = stringPreferencesKey("dogName")

        private suspend fun editDogId(dogId: Long) {
            context.dogDataStore.edit { preferences ->
                preferences[preferenceDogId] = dogId
            }
        }

        private suspend fun editDogName(dogName: String) {
            context.dogDataStore.edit { preferences ->
                preferences[preferenceDogName] = dogName
            }
        }

        fun setDogId(dogId: Long) {
            CoroutineScope(Dispatchers.IO).launch {
                editDogId(dogId)
            }
        }

        fun setDogName(dogName: String) {
            CoroutineScope(Dispatchers.IO).launch {
                editDogName(dogName)
            }
        }

        val dogId: Flow<Long?> =
            context.dogDataStore.data.map { preferences ->
                preferences[preferenceDogId]
            }

        val dogName: Flow<String?> =
            context.dogDataStore.data.map { preferences ->
                preferences[preferenceDogName]
            }
    }
