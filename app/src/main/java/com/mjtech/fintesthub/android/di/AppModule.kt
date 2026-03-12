package com.mjtech.fintesthub.android.di

import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.payment.repository.PaymentRepository
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import com.mjtech.domain.settings.repository.TefAdminAction
import com.mjtech.fintesthub.android.FinApplication
import com.mjtech.fintesthub.android.data.payment.repository.MockPaymentRepository
import com.mjtech.fintesthub.android.data.settings.core.MainSettingsKeys.ENVIRONMENT_TYPE
import com.mjtech.fintesthub.android.data.settings.repository.MockSettingsRepository
import com.mjtech.fintesthub.android.ui.checkout.CheckoutViewModel
import com.mjtech.fintesthub.android.ui.settings.SettingsViewModel
import com.mjtech.fintesthub.android.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    factory<PaymentProcessor> {
        val currentEnv = Settings.getValue(
            ENVIRONMENT_TYPE,
            defaultValue = FinApplication.Environment.MSITEF.value
        )

        if (currentEnv == FinApplication.Environment.MSITEF.value) {
            get(named("msitef"))
        } else {
            get(named("clisitef"))
        }
    }

    factory<TefAdminAction> {
        val currentEnv = Settings.getValue(
            ENVIRONMENT_TYPE,
            defaultValue = FinApplication.Environment.MSITEF.value
        )

        if (currentEnv == FinApplication.Environment.MSITEF.value) {
            get(named("msitef"))
        } else {
            get(named("clisitef"))
        }
    }


    single<PaymentRepository> { MockPaymentRepository() }

    single<SettingsRepository> { MockSettingsRepository(get()) }

    viewModel { SplashViewModel(get()) }

    viewModel { CheckoutViewModel(get(), get(), get()) }

    viewModel { SettingsViewModel(get(), get(), get()) }
}