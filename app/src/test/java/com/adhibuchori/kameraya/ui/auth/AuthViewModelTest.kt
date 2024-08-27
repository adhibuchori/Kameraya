package com.adhibuchori.kameraya.ui.auth

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.auth.IAuthRepository
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.adhibuchori.kameraya.utils.MainDispatcherRule
import com.adhibuchori.kameraya.utils.firebase.FirebaseConstant
import com.adhibuchori.kameraya.utils.getOrAwaitValue
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {
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

    private suspend fun createViewModel(): AuthViewModel {
        `when`(authRepository.isLogin()).thenReturn(flowOf("ribbon@gmail.com"))
        `when`(authRepository.setUserName()).thenReturn(flowOf("リボン"))
        `when`(authRepository.setUserImage()).thenReturn(flowOf("ribbon_image_url"))
        `when`(authRepository.readOnBoardingShown()).thenReturn(flowOf(true))

        return AuthViewModel(authRepository, firebaseAnalytics)
    }

    @Test
    fun `loginStatus should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedLoginStatus = "ribbon@gmail.com"

        val observer = Observer<String?> { value ->
            assertEquals(expectedLoginStatus, value)
        }
        viewModel.loginStatus.observeForever(observer)

        advanceUntilIdle()
        viewModel.loginStatus.removeObserver(observer)
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
    fun `onBoardingShown should emit the value returned by authRepository`() = runTest {
        val viewModel = createViewModel()
        val expectedOnBoardingShown = true

        val observer = Observer<Boolean?> { value ->
            assertEquals(expectedOnBoardingShown, value)
        }
        viewModel.onBoardingShown.observeForever(observer)

        advanceUntilIdle()
        viewModel.onBoardingShown.removeObserver(observer)
    }

    @Test
    fun `login should update loginResult LiveData on success`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val email = "ribbon@gmail.com"
        val password = "12345678"

        val expectedResult = Resource.Success(true)
        `when`(authRepository.login(email, password)).thenReturn(expectedResult)

        viewModel.login(email, password)
        advanceUntilIdle()

        assertEquals(expectedResult, viewModel.loginResult.getOrAwaitValue())
    }

    @Test
    fun `register should update registerResult LiveData on success`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val email = "ribbon@gmail.com"
        val password = "12345678"
        val userName = "リボン"
        val userImageFile = File("ribbon_image.jpg")

        val registerResult = Resource.Success(true)
        val profileResult = Resource.Success(true)

        `when`(authRepository.register(email, password)).thenReturn(registerResult)
        `when`(authRepository.profile(userName, userImageFile)).thenReturn(profileResult)

        viewModel.register(email, password, userName, userImageFile)
        advanceUntilIdle()

        assertEquals(profileResult, viewModel.registerResult.getOrAwaitValue())
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
