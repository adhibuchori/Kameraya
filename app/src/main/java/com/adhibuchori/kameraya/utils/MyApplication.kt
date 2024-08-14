package com.adhibuchori.kameraya.utils

import android.app.Application
import com.adhibuchori.data.utils.module.databaseModule
import com.adhibuchori.di.appModule
import com.adhibuchori.di.networkModule
import com.adhibuchori.kameraya.utils.module.viewModel
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
            modules(appModule, viewModel, networkModule, databaseModule)
        }
    }
}