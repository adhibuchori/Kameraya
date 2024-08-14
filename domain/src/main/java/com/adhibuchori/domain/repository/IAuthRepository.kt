package com.adhibuchori.domain.repository

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
}