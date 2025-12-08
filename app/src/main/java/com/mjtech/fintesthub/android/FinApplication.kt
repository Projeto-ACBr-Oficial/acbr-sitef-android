package com.mjtech.fintesthub.android

import android.app.Application
import com.mjtech.domain.settings.model.Setting
import com.mjtech.fintesthub.android.data.settings.core.MainSettingsKeys.PRINT_RECEIPT
import com.mjtech.fintesthub.android.di.appModule
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.CNPJ_AUTOMACAO
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.CNPJ_CPF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.EMPRESA_SITEF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.ENDERECO_SITEF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.OPERADOR
import com.mjtech.fiserv.msitef.di.msitefModule
import com.mjtech.print.di.sunmiPrinterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinApplication : Application() {

    companion object {
        /**
         * Configuração padrão e/ou inicial
         */
        val DEFAULT_SETTINGS = listOf(
            Setting(key = PRINT_RECEIPT, value = true),
            Setting(key = EMPRESA_SITEF, value = "00000000"),
            Setting(key = ENDERECO_SITEF, value = "127.0.0.1"),
            Setting(key = OPERADOR, value = "0001"),
            Setting(key = CNPJ_CPF, value = "12345678912345"),
            Setting(key = CNPJ_AUTOMACAO, value = "12345678912345"),
        )
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