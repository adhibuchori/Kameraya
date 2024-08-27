package com.adhibuchori.data.auth.interceptor

import com.adhibuchori.data.utils.preference.AppPreference
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor(
    private val appPreference: AppPreference,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)

        if (response.code == 401) {
            runBlocking { appPreference.logout() }
            return response
        }

        return response
    }
}