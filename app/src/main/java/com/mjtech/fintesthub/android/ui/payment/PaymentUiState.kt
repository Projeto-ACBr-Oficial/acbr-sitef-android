package com.mjtech.fintesthub.android.ui.payment

data class PaymentUiState(
    val valueInCents: Long = 0L,
    val formattedValue: String = "R$ 0,00",
    val errorMessage: String? = null
)