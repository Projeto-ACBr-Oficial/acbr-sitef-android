package com.mjtech.fintesthub.android.ui.settings

import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.Settings

data class SettingsUiState(
    val settingsModel: Settings? = null,
    val settingsResult: Result<Settings>? = null,
    val saveSettingsResult: Result<Unit>? = null,
    val isLoading: Boolean = true,
    val showSuccessMessage: Boolean = false
)