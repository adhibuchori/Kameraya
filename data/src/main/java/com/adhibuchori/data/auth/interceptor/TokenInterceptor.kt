package com.adhibuchori.data.auth.interceptor

import com.adhibuchori.data.BuildConfig
import com.adhibuchori.data.utils.preference.AppPreference
import com.adhibuchori.data.auth.request.RefreshRequest
import com.adhibuchori.data.auth.response.RefreshResponse
import com.adhibuchori.data.auth.source.AuthApiService
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenInterceptor(
    private val appPreference: AppPreference,
    private val chuckInterceptor: ChuckerInterceptor,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            appPreference.getRefreshToken().firstOrNull()
        }

        synchronized(this) {
            return runBlocking {
                try {
                    val newToken = refreshToken(RefreshRequest(refreshToken))
                    appPreference.saveRefreshToken(newToken.data?.refreshToken ?: "")
                    appPreference.saveAccessToken(newToken.data?.accessToken ?: "")
                    response.request
                        .newBuilder()
                        .header("Authorization", "Bearer ${newToken.data?.accessToken}")
                        .build()
                } catch (error: Throwable) {
                    response.close()
                    null
                }
            }
        }
    }

    private suspend fun refreshToken(tokenRequest: RefreshRequest): RefreshResponse {
        val interceptor = Interceptor.invoke { chain ->
            val request = chain
                .request()
                .newBuilder()
                .addHeader("API_KEY", BuildConfig.API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(chuckInterceptor)
            .addInterceptor(interceptor)
            .build()

        val apiService = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AuthApiService::class.java)

        try {
            val newRequest = apiService.refresh(tokenRequest)
            appPreference.saveAccessToken(newRequest.data?.accessToken ?: "")
            appPreference.saveRefreshToken(newRequest.data?.refreshToken ?: "")
            return newRequest
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}