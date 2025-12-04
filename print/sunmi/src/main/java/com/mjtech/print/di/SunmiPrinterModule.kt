package com.mjtech.print.di

import android.content.Context
import com.mjtech.print.data.repository.SunmiPrinterRepository
import com.mjtech.print.data.source.SunmiPrinterManager
import com.mjtech.store.domain.print.repository.PrintRepository
import org.koin.dsl.module

fun sunmiPrinterModule(context: Context) = module {

    single<SunmiPrinterManager> { SunmiPrinterManager.getInstance(context) }

    single<PrintRepository> { SunmiPrinterRepository(get()) }
}