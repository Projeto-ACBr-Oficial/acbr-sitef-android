package com.mjtech.fiserv.msitef.settings

import android.content.Context
import android.content.SharedPreferences
private const val PREFS_NAME = "app_settings"

fun Context.getAppSettingsPrefs(): SharedPreferences {
    return this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

object SettingsKeys {
    val INTEGRATION_TYPE = "integration_type"

    val EMPRESA_SITEF = "msitef-empresaSitef"
    val ENDERECO_SITEF = "msitef-enderecoSitef"
    val OPERADOR = "msitef-operador"
    val CNPJ_CPF = "msitef-cnpjCpf"
    val CNPJ_AUTOMACAO = "msitef-cnpjAutomacao"
    val TIMEOUT_COLETA = "msitef-timeoutColeta"
}