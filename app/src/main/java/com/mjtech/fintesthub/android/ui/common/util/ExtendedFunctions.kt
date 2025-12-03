package com.mjtech.fintesthub.android.ui.common.util

import java.text.NumberFormat
import java.util.Locale

fun Double.toCurrencyFormat(): String {
    val format = NumberFormat.getCurrencyInstance(
        Locale.Builder().setLanguage("pt").setRegion("BR").build()
    )
    return format.format(this)
}

fun Long.toCurrencyFormat(): String {
    val format = NumberFormat.getCurrencyInstance(
        Locale.Builder().setLanguage("pt").setRegion("BR").build()
    )
    return format.format(this / 100.0)
}