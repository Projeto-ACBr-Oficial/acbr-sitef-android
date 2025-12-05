package com.mjtech.domain.settings.repository

import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSettings(): Flow<Result<Settings>>

    fun saveSettings(settings: Settings): Flow<Result<Unit>>
}