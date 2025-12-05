package com.mjtech.fintesthub.android

import android.app.Application
import com.mjtech.fintesthub.android.di.appModule
import com.mjtech.fiserv.msitef.di.msitefModule
import com.mjtech.print.di.sunmiPrinterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinApplication : Application() {

    companion object {
        var printReceipt: Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinApplication)
            modules(
                appModule,
                msitefModule,
                sunmiPrinterModule
            )
        }
    }
}