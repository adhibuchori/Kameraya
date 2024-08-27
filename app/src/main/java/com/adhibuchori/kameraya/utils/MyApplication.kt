package com.adhibuchori.kameraya.utils

import android.app.Application
import com.adhibuchori.data.di.databaseModule
import com.adhibuchori.data.di.repositoryModule
import com.adhibuchori.data.di.networkModule
import com.adhibuchori.kameraya.utils.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.NONE)
            androidContext(this@MyApplication)
            modules(repositoryModule, viewModelModule, networkModule, databaseModule)
        }
    }
}