package com.mjtech.fintesthub.android.data.settings.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.Setting
import com.mjtech.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fin_settings")

class MockSettingsRepository(context: Context) : SettingsRepository {

    private val dataStore = context.dataStore

    override fun getSetting(setting: Setting): Flow<Result<Setting>> {
        return dataStore.data
            .catch { exception ->
                Result.Error(exception.message.toString())
            }
            .map { preferences ->
                val key = setting.key
                val defaultValue = setting.value

                val value = when (defaultValue) {
                    is String -> preferences[stringPreferencesKey(key)] ?: defaultValue
                    is Boolean -> preferences[booleanPreferencesKey(key)] ?: defaultValue
                    is Int -> preferences[intPreferencesKey(key)] ?: defaultValue
                    is Long -> preferences[longPreferencesKey(key)] ?: defaultValue
                    is Double -> preferences[doublePreferencesKey(key)] ?: defaultValue
                    else -> throw IllegalArgumentException("Tipo de valor padrão desconhecido para busca.")
                }
                Result.Success(Setting(key = key, value = value))
            }
    }

    override fun saveSetting(setting: Setting): Flow<Result<Unit>> = flow {
        dataStore.edit { preferences ->
            when (setting.value) {
                is String -> {
                    preferences[stringPreferencesKey(setting.key)] = setting.value as String
                }
                is Boolean -> {
                    preferences[booleanPreferencesKey(setting.key)] = setting.value as Boolean
                }
                is Int -> {
                    preferences[intPreferencesKey(setting.key)] = setting.value as Int
                }
                is Long -> {
                    preferences[longPreferencesKey(setting.key)] = setting.value as Long
                }
                is Double -> {
                    preferences[doublePreferencesKey(setting.key)] = setting.value as Double
                }
                else -> {
                    throw IllegalArgumentException("Tipo de valor (${setting.value.javaClass.simpleName}) não suportado pelo DataStore.")
                }
            }
        }
        emit(Result.Success(Unit))
    }
}