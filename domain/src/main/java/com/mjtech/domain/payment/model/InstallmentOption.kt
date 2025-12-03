package com.mjtech.domain.payment.model

import kotlinx.serialization.Serializable

/**
 * @param count Número da parcela.
 * @param totalAmount Valor total da parcela.
 * @param interestRate Taxa de juros aplicada.
 * @param monthlyAmount Valor da parcela mensal.
 */
@Serializable
data class InstallmentOption(
    val count: Int,
    val totalAmount: Double,
    val interestRate: Double = 0.0,
    val monthlyAmount: Double
)
