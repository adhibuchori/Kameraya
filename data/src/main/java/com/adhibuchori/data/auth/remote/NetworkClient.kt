package com.adhibuchori.data.auth.remote

import com.adhibuchori.data.BuildConfig
import com.adhibuchori.data.auth.interceptor.AuthInterceptor
import com.adhibuchori.data.auth.interceptor.SessionInterceptor
import com.adhibuchori.data.auth.interceptor.TokenInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class

NetworkClient(
    val authInterceptor: AuthInterceptor,
    val sessionInterceptor: SessionInterceptor,
    val tokenInterceptor: TokenInterceptor,
    val chuckerInterceptor: ChuckerInterceptor,
) {
    inline fun <reified I> create(): I {
        val okhttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(sessionInterceptor)
            .authenticator(tokenInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient)
            .build()

        return retrofit.create(I::class.java)
    }
}