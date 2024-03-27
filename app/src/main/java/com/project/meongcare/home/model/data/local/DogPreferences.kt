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

        suspend fun editDogId(dogId: Long?) {
            context.dogDataStore.edit { preferences ->
                if (dogId == null) {
                    preferences.remove(preferenceDogId)
                } else {
                    preferences[preferenceDogId] = dogId
                }
            }
        }

        suspend fun editDogName(dogName: String?) {
            context.dogDataStore.edit { preferences ->
                if (dogName == null) {
                    preferences.remove(preferenceDogName)
                } else {
                    preferences[preferenceDogName] = dogName
                }
            }
        }

        suspend fun editDogWeight(dogWeight: Double?) {
            context.dogDataStore.edit { preferences ->
                if (dogWeight == null) {
                    preferences.remove(preferenceDogWeight)
                } else {
                    preferences[preferenceDogWeight] = dogWeight
                }
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
