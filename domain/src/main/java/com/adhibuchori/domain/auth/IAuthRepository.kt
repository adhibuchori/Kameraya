package com.adhibuchori.domain.auth

import com.adhibuchori.domain.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IAuthRepository {
    suspend fun login(email: String, password: String): Resource<Boolean>
    suspend fun register(email: String, password: String): Resource<Boolean>
    suspend fun profile(userName: String, userImageFile: File): Resource<Boolean>
    suspend fun isLogin(): Flow<String?>
    suspend fun setUserName(): Flow<String?>
    suspend fun setUserImage(): Flow<String?>
    suspend fun setEmail(): Flow<String?>
    suspend fun logout(): Flow<Resource<String?>>

    fun setTheme(): Flow<Boolean?>
    suspend fun saveThemeSetting(isDarkModeActive: Boolean)

    fun setLanguage(): Flow<Boolean?>
    suspend fun saveLanguageSetting(isLanguageChangeActive: Boolean)

    fun readOnBoardingShown(): Flow<Boolean?>
    suspend fun saveOnBoardingShown(isShown: Boolean)
}