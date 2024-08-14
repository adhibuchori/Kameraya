package com.adhibuchori.kameraya.ui.auth.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getLanguageSetting(): LiveData<Boolean> {
        return pref.getLanguageSetting().asLiveData()
    }

    fun saveLanguageSetting(isLanguageChangeActive: Boolean) {
        viewModelScope.launch {
            pref.saveLanguageSetting(isLanguageChangeActive)
        }
    }

    fun getOnBoardingShown(): LiveData<Boolean> {
        return pref.readOnBoardingShown()
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun saveOnBoardingShown(isShown: Boolean) {
        viewModelScope.launch {
            pref.saveOnBoardingShown(isShown)
        }
    }
}