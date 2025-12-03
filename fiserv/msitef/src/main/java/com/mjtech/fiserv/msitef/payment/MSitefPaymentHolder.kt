package com.mjtech.fiserv.msitef.payment

import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.repository.PaymentCallback

object MSitefPaymentHolder {
    var callback: PaymentCallback? = null
    var payment: Payment? = null

    fun initialize(callback: PaymentCallback, payment: Payment) {
        this.callback = callback
        this.payment = payment
    }

    fun clear() {
        this.callback = null
        this.payment = null
    }
}