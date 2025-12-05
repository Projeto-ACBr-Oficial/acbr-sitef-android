package com.mjtech.fintesthub.android.di

import com.mjtech.domain.payment.repository.PaymentRepository
import com.mjtech.fintesthub.android.data.payment.repository.MockPaymentRepository
import com.mjtech.fintesthub.android.ui.checkout.CheckoutViewModel
import com.mjtech.fintesthub.android.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<PaymentRepository> { MockPaymentRepository() }

    viewModel { CheckoutViewModel(get(), get(), get()) }

    viewModel { SettingsViewModel(get(), get()) }


}