package com.mjtech.fintesthub.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings()
                .collect { result ->
                _uiState.update {
                    it.copy(
                        settingsResult = result,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepository.saveSettings(settings).collect { result ->
                _uiState.update {
                    it.copy(
                        saveSettingsResult = result
                    )
                }
            }
        }
    }
}