package com.mjtech.print.di

import com.mjtech.domain.print.repository.PrintRepository
import com.mjtech.print.data.repository.SunmiPrinterRepository
import com.mjtech.print.data.source.SunmiPrinterManager
import org.koin.dsl.module

val sunmiPrinterModule = module {

    single<SunmiPrinterManager> { SunmiPrinterManager.getInstance(get()) }

    single<PrintRepository> { SunmiPrinterRepository(get()) }
}