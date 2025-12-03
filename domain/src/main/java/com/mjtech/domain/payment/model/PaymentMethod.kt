package com.mjtech.domain.payment.model

import kotlinx.serialization.Serializable

/**
 * @param id O ID do método de pagamento.
 * @param name O nome do método de pagamento.
 * @param maxInstallments A quantidade máxima de parcelas permitidas.
 * @param requiresInstallments Indica se o método de pagamento permite parcelamento.
 */
@Serializable
data class PaymentMethod(
    val id: String,
    val name: String,
    val maxInstallments: Int = 1,
    val requiresInstallments: Boolean = false
)
