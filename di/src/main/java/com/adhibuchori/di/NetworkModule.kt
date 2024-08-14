package com.adhibuchori.di

import com.adhibuchori.data.auth.interceptor.AuthInterceptor
import com.adhibuchori.data.auth.interceptor.SessionInterceptor
import com.adhibuchori.data.auth.interceptor.TokenInterceptor
import com.adhibuchori.data.auth.remote.NetworkClient
import com.adhibuchori.data.auth.source.AuthApiService
import com.adhibuchori.data.productDetail.source.ProductDetailApiService
import com.adhibuchori.data.store.source.StoreApiService
import com.chuckerteam.chucker.api.ChuckerInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single { ChuckerInterceptor.Builder(androidContext()).build() }
    single { AuthInterceptor(get()) }
    single { SessionInterceptor(get()) }
    single { TokenInterceptor(get(), get()) }
    single { NetworkClient(get(), get(), get(), get()) }
    single<AuthApiService> { get<NetworkClient>().create() }
    single<StoreApiService> { get<NetworkClient>().create() }
    single<ProductDetailApiService> { get<NetworkClient>().create() }
}