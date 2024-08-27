package com.adhibuchori.kameraya.ui.onboarding

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.auth.IAuthRepository
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val authRepository: IAuthRepository,
    private val firebaseAnalytics: IFirebaseAnalyticsRepository,
) : ViewModel() {

    fun saveOnBoardingShown(isShown: Boolean) {
        viewModelScope.launch {
            authRepository.saveOnBoardingShown(isShown)
        }
    }

    fun logScreenView(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }

}