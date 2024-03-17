package com.project.meongcare.home.model.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.meongcare.login.view.dogDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class DogPreferences
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val preferenceDogId = longPreferencesKey("dogId")
        private val preferenceDogName = stringPreferencesKey("dogName")
        private val preferenceDogWeight = doublePreferencesKey("dogWeight")

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

        private suspend fun editDogWeight(dogWeight: Double) {
            context.dogDataStore.edit { preferences ->
                preferences[preferenceDogWeight] = dogWeight
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

        fun setDogWeight(dogWeight: Double) {
            CoroutineScope(Dispatchers.IO).launch {
                editDogWeight(dogWeight)
            }
        }

        val dogId: Flow<Long?> =
            context.dogDataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[preferenceDogId]
            }

        val dogName: Flow<String?> =
            context.dogDataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[preferenceDogName]
            }

        val dogWeight: Flow<Double?> =
            context.dogDataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[preferenceDogWeight]
            }
    }
