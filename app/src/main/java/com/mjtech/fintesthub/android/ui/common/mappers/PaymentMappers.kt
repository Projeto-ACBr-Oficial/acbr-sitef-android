package com.mjtech.fintesthub.android.ui.common.mappers

import androidx.compose.ui.graphics.Color
import com.mjtech.domain.payment.model.PaymentMethod
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.checkout.PaymentMethodUi

fun PaymentMethod.toUi(): PaymentMethodUi {
    val (iconRes, color, description) = getUiAssetsForPaymentType(this.id)

    return PaymentMethodUi(
        id = this.id,
        name = this.name,
        description = description,
        icon = iconRes,
        color = color
    )
}

private fun getUiAssetsForPaymentType(methodId: String): Triple<Int, Color, String> {
    return when (methodId) {
        "DEBIT" -> Triple(R.drawable.ic_credit_card, Color.Red, "Cartão de Débito")
        "CREDIT" -> Triple(R.drawable.ic_credit_card, Color.Green, "Cartão de Crédito")
        "PIX" -> Triple(R.drawable.ic_bank, Color.Magenta, "PIX e carteiras digitais")
        "VOUCHER" -> Triple(R.drawable.ic_credit_card, Color.Blue, "Vale alimentação ou refeição")
        else -> Triple(R.drawable.ic_credit_card, Color.Gray, "Método desconhecido")
    }
}
