package com.mjtech.fintesthub.android.data.payment.repository

import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.repository.PaymentCallback
import com.mjtech.domain.payment.repository.PaymentProcessor

class MockPaymentProcessor: PaymentProcessor {

    override fun processPayment(
        payment: Payment,
        callback: PaymentCallback
    ) {
       if (payment.amount <= 1000) {
           callback.onSuccess(
               transactionId = "MOCK-${System.currentTimeMillis()}",
               message = "Payment processed successfully."
           )
       } else {
           callback.onFailure(
               errorCode = "LIMIT_EXCEEDED",
               errorMessage = "Payment amount exceeds the limit."
           )
       }
    }
}