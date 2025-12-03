package com.mjtech.fintesthub.android

import android.app.Application
import com.mjtech.fintesthub.android.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinApplication)
            modules(
                appModule
            )
        }
    }
}