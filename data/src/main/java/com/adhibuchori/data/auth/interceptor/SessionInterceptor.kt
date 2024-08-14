package com.adhibuchori.data.auth.interceptor

import com.adhibuchori.data.auth.preference.AuthPreference
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor(
    private val authPreference: AuthPreference,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)

        if (response.code == 401) {
            runBlocking { authPreference.logout() }
            return response
        }

        return response
    }
}