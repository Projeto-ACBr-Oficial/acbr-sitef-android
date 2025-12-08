package com.mjtech.fintesthub.android.ui.settings

import com.mjtech.domain.common.Result

data class SettingsUiState(
    val editableSettings: EditableSettings = emptyMap(),
    val printResult: Result<Unit>? = null,
    val saveSettingsResult: Result<Unit>? = null,
    val isLoading: Boolean = true,
    val showSuccessMessage: Boolean = false
)