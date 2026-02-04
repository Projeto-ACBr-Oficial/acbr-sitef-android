package com.mjtech.fintesthub.android.ui.checkout.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class PaymentMethodUi(
    val id: String,
    val name: String,
    val description: String,
    val color: Color,
    @DrawableRes val icon: Int,
)