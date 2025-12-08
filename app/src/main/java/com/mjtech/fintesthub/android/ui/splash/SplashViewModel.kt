package com.mjtech.fintesthub.android.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.common.Result
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import com.mjtech.fintesthub.android.FinApplication.Companion.DEFAULT_SETTINGS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        initializeSettings()
    }

    private fun initializeSettings() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                for (defaultSetting in DEFAULT_SETTINGS) {
                    val result = settingsRepository.getSetting(defaultSetting).first()

                    if (result is Result.Success) {
                        val loadedSetting = result.data

                        settingsRepository.saveSetting(loadedSetting).first()
                        Settings.updateSetting(loadedSetting)

                    } else if (result is Result.Error) {
                        Settings.updateSetting(defaultSetting)
                        settingsRepository.saveSetting(defaultSetting).first()
                    }
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isReadyToNavigate = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Falha crítica ao carregar configurações: ${e.message}"
                    )
                }
            }
        }
    }
}