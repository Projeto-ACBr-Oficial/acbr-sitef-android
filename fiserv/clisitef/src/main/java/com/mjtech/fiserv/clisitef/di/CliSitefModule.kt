package com.mjtech.fiserv.clisitef.di

import com.mjtech.domain.settings.repository.TefAdminAction
import com.mjtech.fiserv.clisitef.settings.CliSitefAdminHandler
import org.koin.core.qualifier.named
import org.koin.dsl.module

val clisitefModule = module {

    single<TefAdminAction>(named("clisitef")) { CliSitefAdminHandler(get()) }
}