package com.mjtech.domain.payment.repository

import com.mjtech.domain.payment.model.Payment

interface PaymentProcessor {

    fun processPayment(payment: Payment, callback: PaymentCallback)
}