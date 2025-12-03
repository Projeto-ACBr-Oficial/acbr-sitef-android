package com.mjtech.domain.payment.repository

import com.mjtech.domain.common.Result
import com.mjtech.domain.payment.model.InstallmentOption
import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.model.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun getAvailablePaymentMethods(): Flow<Result<List<PaymentMethod>>>

    fun getInstallmentOptions(
        methodId: String,
        totalAmount: Double
    ): Flow<Result<List<InstallmentOption>>>

    fun processPayment(payment: Payment): Flow<Result<Unit>>
}