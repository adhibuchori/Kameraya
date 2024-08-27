package com.adhibuchori.domain.preference

import kotlinx.coroutines.flow.Flow

interface IAppPreferences {
    fun getThemeSetting(): Flow<Boolean>
    suspend fun saveThemeSetting(isDarkModeActive: Boolean)

    fun getLanguageSetting(): Flow<Boolean>
    suspend fun saveLanguageSetting(isLanguageChangeActive: Boolean)

    fun readOnBoardingShown(): Flow<Boolean>
    suspend fun saveOnBoardingShown(isShown: Boolean)
}