package com.mjtech.fiserv.msitef.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Retorna a data atual no formato "yyyyMMdd" - padrão esperado pelo SiTef */
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

/** Retorna a hora atual no formato "HHmmss" - padrão esperado pelo SiTef */
fun getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
    val date = Date()
    return timeFormat.format(date)
}

/** Converte um valor Double para String sem pontos, com duas casas decimais - padrão esperado pelo SiTef */
fun Double.toStringWithoutDots(): String {
    val valueFormat = String.format(Locale.ROOT, "%.2f", this)
    return valueFormat.replace(".", "")
}