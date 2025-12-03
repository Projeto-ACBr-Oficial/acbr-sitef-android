package com.mjtech.fiserv.msitef.di

import com.mjtech.fiserv.msitef.payment.MSitefPaymentProcessor
import com.mjtech.domain.payment.repository.PaymentProcessor
import org.koin.dsl.module

val msitefModule = module {

    single<PaymentProcessor> { MSitefPaymentProcessor(get()) }
}