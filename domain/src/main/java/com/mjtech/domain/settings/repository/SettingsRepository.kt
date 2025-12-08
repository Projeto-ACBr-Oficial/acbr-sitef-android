package com.mjtech.domain.settings.repository

import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.Setting
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSetting(setting: Setting): Flow<Result<Setting>>

    fun saveSetting(setting: Setting): Flow<Result<Unit>>
}