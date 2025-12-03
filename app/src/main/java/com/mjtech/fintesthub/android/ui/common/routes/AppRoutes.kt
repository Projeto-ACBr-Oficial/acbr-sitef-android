package com.mjtech.fintesthub.android.ui.common.routes

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data object HomeRoute

@Serializable
data object PaymentRoute

@Serializable
data object ConfigurationRoute

@Serializable
data class CheckoutRoute(val valueInCents: Long)