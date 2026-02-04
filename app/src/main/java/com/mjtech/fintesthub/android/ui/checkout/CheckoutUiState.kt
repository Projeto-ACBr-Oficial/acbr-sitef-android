package com.mjtech.fintesthub.android.ui.checkout

import com.mjtech.domain.common.Result
import com.mjtech.domain.payment.model.InstallmentOption
import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.model.PaymentMethod
import com.mjtech.fintesthub.android.ui.checkout.models.PaymentMethodUi
import com.mjtech.fintesthub.android.ui.checkout.models.TransactionResultUi

data class CheckoutUiState(
    val transactionAmount: Double = 0.0,
    val selectedPaymentMethodId: String? = null,
    val paymentMethods: Result<List<PaymentMethod>>? = null,
    val availableUiMethods: List<PaymentMethodUi> = emptyList(),
    val installmentsOptions: Result<List<InstallmentOption>>? = null,
    val isPrintEnabled: Boolean = false,
    val printResult: Result<Unit>? = null,
    val isLoading: Boolean = false,
    val payment: Payment? = null,
    val transactionResult: TransactionResultUi? = null
) {
    val availablePaymentMethods: List<PaymentMethod>
        get() = (paymentMethods as? Result.Success)?.data ?: emptyList()
}