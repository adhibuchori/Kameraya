package com.adhibuchori.kameraya.ui.onboarding

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adhibuchori.domain.auth.IAuthRepository
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.kameraya.utils.MainDispatcherRule
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class OnBoardingViewModelTest {

    @Mock
    private lateinit var firebaseAnalytics: IFirebaseAnalyticsRepository

    @Mock
    private lateinit var authRepository: IAuthRepository

    @Mock
    private lateinit var bundle: Bundle

    @get:Rule
    var mainDispatcher = MainDispatcherRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private fun createViewModel(): OnBoardingViewModel {
        return OnBoardingViewModel(authRepository, firebaseAnalytics)
    }

    @Test
    fun `saveOnBoardingShown should call authRepository with correct parameter`() = runTest {
        val viewModel = createViewModel()

        val isShown = true
        viewModel.saveOnBoardingShown(isShown)
        advanceUntilIdle()

        verify(authRepository).saveOnBoardingShown(isShown)
    }

    @Test
    fun `logScreenView should call firebaseAnalytics with SCREEN_VIEW event`() = runTest {
        val viewModel = createViewModel()
        viewModel.logScreenView(bundle)
        verify(firebaseAnalytics).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    @Test
    fun `logButtonEvent should call firebaseAnalytics with BUTTON_CLICK event`() = runTest {
        val viewModel = createViewModel()
        viewModel.logButtonEvent(bundle)
        verify(firebaseAnalytics).logEvent(FirebaseConstant.Event.BUTTON_CLICK, bundle)
    }
}