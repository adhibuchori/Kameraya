package com.adhibuchori.kameraya.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.repository.IAuthRepository
import kotlinx.coroutines.launch
import com.adhibuchori.domain.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File

class AuthViewModel(private val authRepository: IAuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<Boolean>>()
    val loginResult: LiveData<Resource<Boolean>> = _loginResult

    private val _registerResult = MutableLiveData<Resource<Boolean>>()
    val registerResult: LiveData<Resource<Boolean>> = _registerResult

    val loginStatus: LiveData<String?> = runBlocking {
        authRepository.isLogin().asLiveData()
    }

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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginResult = authRepository.login(email, password)
            _loginResult.value = loginResult
        }
    }

    fun register(email: String, password: String, userName: String, userImageFile: File) {
        viewModelScope.launch {
            try {
                val registerResult = authRepository.register(email, password)
                if (registerResult is Resource.Success) {
                    val profileResult = authRepository.profile(userName, userImageFile)
                    _registerResult.value = profileResult
                } else {
                    _registerResult.value = registerResult
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error during registration: ${e.localizedMessage}")
            }
        }
    }
}