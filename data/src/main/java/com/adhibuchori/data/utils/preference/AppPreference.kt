package com.adhibuchori.data.utils.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val tokenKey = stringPreferencesKey("token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val userName = stringPreferencesKey("user_name")
    private val userImage = stringPreferencesKey("user_image")
    private val email = stringPreferencesKey("email")
    private val themeKey = booleanPreferencesKey("theme_setting")
    private val languageKey = booleanPreferencesKey("language_setting")
    private val onboardingShown = booleanPreferencesKey("onBoardingShown")

    suspend fun saveUserImage(userImage: String) {
        dataStore.edit { preferences ->
            preferences[this.userImage] = userImage
        }
    }

    fun getUserImage(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[userImage]
        }
    }

    suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[this.userName] = userName
        }
    }

    fun getUserName(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[userName]
        }
    }

    suspend fun saveEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[this.email] = email
        }
    }

    fun getEmail(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[email]
        }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[tokenKey]
        }
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[refreshTokenKey]
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[refreshTokenKey] = token
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

    suspend fun logout() {
        dataStore.edit {
            it.run {
                remove(tokenKey)
                remove(refreshTokenKey)
                remove(userName)
                remove(userImage)
                remove(email)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}