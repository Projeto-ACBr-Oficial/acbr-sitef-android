package com.mjtech.fintesthub.android.di

import com.mjtech.domain.payment.repository.PaymentRepository
import com.mjtech.domain.settings.repository.SettingsRepository
import com.mjtech.fintesthub.android.data.payment.repository.MockPaymentRepository
import com.mjtech.fintesthub.android.data.settings.repository.MockSettingsRepository
import com.mjtech.fintesthub.android.ui.checkout.CheckoutViewModel
import com.mjtech.fintesthub.android.ui.settings.SettingsViewModel
import com.mjtech.fintesthub.android.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<PaymentRepository> { MockPaymentRepository() }

    single<SettingsRepository> { MockSettingsRepository(get()) }

    viewModel { SplashViewModel(get()) }

    viewModel { CheckoutViewModel(get(), get(), get()) }

    viewModel { SettingsViewModel(get(), get()) }
}