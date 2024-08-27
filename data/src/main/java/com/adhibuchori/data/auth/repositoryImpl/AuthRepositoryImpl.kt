package com.adhibuchori.data.auth.repositoryImpl

import android.util.Log
import com.adhibuchori.data.utils.preference.AppPreference
import com.adhibuchori.data.auth.request.LoginRequest
import com.adhibuchori.data.auth.request.RegisterRequest
import com.adhibuchori.data.auth.source.AuthApiService
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.auth.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class AuthRepositoryImpl(
    private val authAPIService: AuthApiService,
    private val appPreference: AppPreference,
) : IAuthRepository {

    override suspend fun login(email: String, password: String): Resource<Boolean> {
        return try {
            val loginRequest = LoginRequest(email, password)
            val response = authAPIService.login(loginRequest)

            val userName = response.data?.userName.orEmpty()
            val userImage = response.data?.userImage.orEmpty()

            val token = response.data?.accessToken.orEmpty()
            val refreshToken = response.data?.refreshToken.orEmpty()

            appPreference.run {
                saveUserName(userName)
                saveUserImage(userImage)
                saveEmail(email)
                saveAccessToken(token)
                saveRefreshToken(refreshToken)
            }

            Resource.Success(token.isEmpty().not())
        } catch (e: Exception) {
            Log.d("AuthRepository", "login: ${e.message.toString()}")
            when (e) {
                is IOException -> {
                    Resource.NetworkError
                }

                is HttpException -> {
                    Resource.HttpError(e.code(), e.message)
                }

                else -> {
                    Resource.Error(e.message)
                }
            }
        }
    }

    override suspend fun isLogin(): Flow<String?> = appPreference.getAccessToken()
    override suspend fun setUserName(): Flow<String?> = appPreference.getUserName()
    override suspend fun setUserImage(): Flow<String?> = appPreference.getUserImage()
    override suspend fun setEmail(): Flow<String?> = appPreference.getEmail()

    override fun setTheme(): Flow<Boolean> = appPreference.getThemeSetting()
    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        appPreference.saveThemeSetting(isDarkModeActive)
    }

    override fun setLanguage(): Flow<Boolean> = appPreference.getLanguageSetting()
    override suspend fun saveLanguageSetting(isLanguageChangeActive: Boolean) {
        appPreference.saveLanguageSetting(isLanguageChangeActive)
    }

    override fun readOnBoardingShown(): Flow<Boolean> = appPreference.readOnBoardingShown()

    override suspend fun saveOnBoardingShown(isShown: Boolean) {
        appPreference.saveOnBoardingShown(isShown)
    }

    override suspend fun register(email: String, password: String): Resource<Boolean> {
        return try {
            val registerRequest = RegisterRequest(email, password)
            val response = authAPIService.register(registerRequest)

            val token = response.data?.accessToken.orEmpty()
            val refreshToken = response.data?.refreshToken.orEmpty()

            appPreference.run {
                saveEmail(email)
                saveAccessToken(token)
                saveRefreshToken(refreshToken)
            }

            Resource.Success(token.isEmpty().not())
        } catch (e: Exception) {
            Log.d("AuthRepository", "register: ${e.message.toString()}")
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun profile(userName: String, userImageFile: File): Resource<Boolean> {
        return try {
            val userNameRequestBody = userName.toRequestBody("text/plain".toMediaTypeOrNull())
            val userImageRequestBody = userImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val userImagePart = MultipartBody.Part.createFormData(
                "userImage",
                userImageFile.name,
                userImageRequestBody
            )

            val response = authAPIService.profile(userNameRequestBody, userImagePart)

            appPreference.run {
                saveUserName(userName)
                saveUserImage(userImageFile.toString())
            }

            Resource.Success(response.data?.userName.orEmpty().isNotEmpty())

        } catch (e: Exception) {
            Log.d("AuthRepository", "profile: ${e.message.toString()}")
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun logout(): Flow<Resource<String?>> = flow {
        emit(Resource.Loading)
        appPreference.logout()
        emit(Resource.Success("success"))
    }
}