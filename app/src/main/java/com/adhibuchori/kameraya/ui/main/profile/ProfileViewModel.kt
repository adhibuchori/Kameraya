package com.adhibuchori.kameraya.ui.main.profile

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.auth.IAuthRepository
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileViewModel(
    private val authRepository: IAuthRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {

    val userName: LiveData<String?> = runBlocking {
        authRepository.setUserName().asLiveData()
    }

    val userImage: LiveData<String?> = runBlocking {
        authRepository.setUserImage().asLiveData()
    }

    val email: LiveData<String?> = runBlocking {
        authRepository.setEmail().asLiveData()
    }

    val logout: LiveData<Resource<String?>> = runBlocking {
        authRepository.logout().asLiveData()
    }

    val theme: LiveData<Boolean?> = runBlocking {
        authRepository.setTheme().asLiveData()
    }

    val language: LiveData<Boolean?> = runBlocking {
        authRepository.setLanguage().asLiveData()
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            authRepository.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveLanguageSetting(isLanguageChangeActive: Boolean) {
        viewModelScope.launch {
            authRepository.saveLanguageSetting(isLanguageChangeActive)
        }
    }
}