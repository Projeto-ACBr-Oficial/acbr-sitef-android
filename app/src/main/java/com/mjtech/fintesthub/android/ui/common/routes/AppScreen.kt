package com.mjtech.fintesthub.android.ui.common.routes

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.mjtech.fintesthub.android.R
import kotlinx.serialization.Serializable

sealed class AppScreen(
    val route: @Serializable Any,
    val label: String,
    val icon: ImageVector? = null,
    @DrawableRes val painter: Int? = null
) {
    object PaymentNavBar : AppScreen(
        route = PaymentRoute,
        label = "Pagamento",
        painter = R.drawable.ic_credit_card
    )

    object SettingsNavBar : AppScreen(
        route = SettingsRoute,
        label = "Configurações",
        icon = Icons.Filled.Settings
    )
}