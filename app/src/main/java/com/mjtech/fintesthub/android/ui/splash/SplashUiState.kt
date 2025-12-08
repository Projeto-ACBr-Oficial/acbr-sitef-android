package com.mjtech.fintesthub.android.ui.splash

data class SplashUiState(
    val isLoading: Boolean = true,
    val isReadyToNavigate: Boolean = false,
    val error: String? = null
)
