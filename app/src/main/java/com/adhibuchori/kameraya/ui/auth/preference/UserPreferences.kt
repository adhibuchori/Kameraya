package com.adhibuchori.kameraya.ui.auth.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val themeKey = booleanPreferencesKey("theme_setting")
    private val languageKey = booleanPreferencesKey("language_setting")
    private val onboardingShown = booleanPreferencesKey("onBoardingShown")

    fun getLanguageSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[languageKey] ?: false
        }
    }

    suspend fun saveLanguageSetting(isLanguageChangeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[languageKey] = isLanguageChangeActive
        }
    }

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    fun readOnBoardingShown(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[onboardingShown] ?: false
            }
    }

    suspend fun saveOnBoardingShown(isShown: Boolean) {
        dataStore.edit { preferences ->
            preferences[onboardingShown] = isShown
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}