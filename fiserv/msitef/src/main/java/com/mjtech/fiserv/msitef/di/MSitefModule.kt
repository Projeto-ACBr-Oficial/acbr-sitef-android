package com.mjtech.fiserv.msitef.di

import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.settings.repository.TefAdminAction
import com.mjtech.fiserv.msitef.payment.MSitefPaymentProcessor
import com.mjtech.fiserv.msitef.settings.MSitefAdminHandler
import org.koin.dsl.module

val msitefModule = module {

    single<PaymentProcessor> { MSitefPaymentProcessor(get()) }

    single<TefAdminAction> { MSitefAdminHandler(get()) }
}