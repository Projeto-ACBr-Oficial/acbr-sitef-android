package com.mjtech.fiserv.msitef.settings

import android.content.Context
import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MSitefSettingsRepository(
    private val context: Context
): SettingsRepository {


    override fun getSettings(): Flow<Result<Settings>> = flow {
        emit(Result.Loading)
//
//        .catch { exception ->
//            if (exception is IOException) {
//                emit(emptyPreferences())
//            } else {
//                // Relança exceções que não são de I/O
//                throw exception
//            }
//        }
//        .map { preferences ->
//            val settings = mapPreferencesToMSitefSettings(preferences)
//            Result.Success(settings)
//        }
    }

    override fun saveSettings(settings: Settings): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
    }
}