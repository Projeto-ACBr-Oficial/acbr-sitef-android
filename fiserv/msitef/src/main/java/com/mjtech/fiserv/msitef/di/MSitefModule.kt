package com.mjtech.fiserv.msitef.di

import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.settings.repository.TefAdminAction
import com.mjtech.fiserv.msitef.payment.MSitefPaymentProcessor
import com.mjtech.fiserv.msitef.settings.MSitefAdminHandler
import org.koin.core.qualifier.named
import org.koin.dsl.module

val msitefModule = module {

    single<PaymentProcessor>(named("msitef")) { MSitefPaymentProcessor(get()) }

    single<TefAdminAction>(named("msitef")) { MSitefAdminHandler(get()) }
}