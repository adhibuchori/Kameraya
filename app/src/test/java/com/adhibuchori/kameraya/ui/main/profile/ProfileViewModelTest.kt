package com.adhibuchori.kameraya.ui.main.profile

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.auth.IAuthRepository
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.kameraya.utils.MainDispatcherRule
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @Mock
    private lateinit var authRepository: IAuthRepository

    @Mock
    private lateinit var firebaseAnalytics: IFirebaseAnalyticsRepository

    @Mock
    private lateinit var bundle: Bundle

    @get:Rule
    var mainDispatcher = MainDispatcherRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private suspend fun createViewModel(): ProfileViewModel {
        `when`(authRepository.setUserName()).thenReturn(flowOf("リボン"))
        `when`(authRepository.setUserImage()).thenReturn(flowOf("ribbon_image_url"))
        `when`(authRepository.setEmail()).thenReturn(flowOf("ribbon@gmail.com"))
        `when`(authRepository.logout()).thenReturn(flowOf(Resource.Success("Logged out")))
        `when`(authRepository.setTheme()).thenReturn(flowOf(true))
        `when`(authRepository.setLanguage()).thenReturn(flowOf(true))

        return ProfileViewModel(authRepository, firebaseAnalytics)
    }

    @Test
    fun `userName should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedUserName = "リボン"

        val observer = Observer<String?> { value ->
            assertEquals(expectedUserName, value)
        }
        viewModel.userName.observeForever(observer)

        advanceUntilIdle()
        viewModel.userName.removeObserver(observer)
    }

    @Test
    fun `userImage should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedUserImage = "ribbon_image_url"

        val observer = Observer<String?> { value ->
            assertEquals(expectedUserImage, value)
        }
        viewModel.userImage.observeForever(observer)

        advanceUntilIdle()
        viewModel.userImage.removeObserver(observer)
    }

    @Test
    fun `email should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedEmail = "ribbon@gmail.com"

        val observer = Observer<String?> { value ->
            assertEquals(expectedEmail, value)
        }
        viewModel.email.observeForever(observer)

        advanceUntilIdle()
        viewModel.email.removeObserver(observer)
    }

    @Test
    fun `logout should emit the success resource returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedLogoutResult = Resource.Success("Logged out")

        val observer = Observer<Resource<String?>> { value ->
            assertEquals(expectedLogoutResult, value)
        }
        viewModel.logout.observeForever(observer)

        advanceUntilIdle()
        viewModel.logout.removeObserver(observer)
    }

    @Test
    fun `theme should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedTheme = true

        val observer = Observer<Boolean?> { value ->
            assertEquals(expectedTheme, value)
        }
        viewModel.theme.observeForever(observer)

        advanceUntilIdle()
        viewModel.theme.removeObserver(observer)
    }

    @Test
    fun `language should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedLanguage = true

        val observer = Observer<Boolean?> { value ->
            assertEquals(expectedLanguage, value)
        }
        viewModel.language.observeForever(observer)

        advanceUntilIdle()
        viewModel.language.removeObserver(observer)
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

    @Test
    fun `saveThemeSetting should call authRepository with correct parameter`() = runTest {
        val viewModel = createViewModel()
        val isDarkModeActive = true

        viewModel.saveThemeSetting(isDarkModeActive)
        advanceUntilIdle()

        verify(authRepository).saveThemeSetting(isDarkModeActive)
    }

    @Test
    fun `saveLanguageSetting should call authRepository with correct parameter`() = runTest {
        val viewModel = createViewModel()
        val isLanguageChangeActive = true

        viewModel.saveLanguageSetting(isLanguageChangeActive)
        advanceUntilIdle()

        verify(authRepository).saveLanguageSetting(isLanguageChangeActive)
    }
}
