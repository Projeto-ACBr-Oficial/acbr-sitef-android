package com.mjtech.fintesthub.android.ui.settings

import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.MSitefSettings
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fiserv.msitef.common.MSiTefData

data class SettingsUiState(
    val settingsModel: Settings? = null,
    val settingsResult: Result<Settings>? = null,
    val printResult: Result<Unit>? = null,
    val saveSettingsResult: Result<Unit>? = null,
    val isLoading: Boolean = true,
    val showSuccessMessage: Boolean = false
)