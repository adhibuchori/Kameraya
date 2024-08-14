package com.adhibuchori.data.auth.interceptor

import com.adhibuchori.data.BuildConfig
import com.adhibuchori.data.auth.preference.AuthPreference
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authPreference: AuthPreference,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val modifiedRequest = when (request.url.encodedPath) {
            "/login", "/register", "/refresh" -> {
                request
                    .newBuilder()
                    .addHeader("API_KEY", BuildConfig.API_KEY)
                    .build()
            }

            else -> {
                val accessToken = runBlocking {
                    authPreference.getAccessToken().firstOrNull()
                }
                request
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            }
        }

        return chain.proceed(modifiedRequest)
    }
}