package com.mjtech.fintesthub.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.settings.model.MSitefSettings
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import com.mjtech.fintesthub.android.FinApplication
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


    private fun updateSettings(newSettings: MSitefSettings) {
        _uiState.update {
            it.copy(settings = newSettings)
        }
    }

    fun updateEmpresaSitef(newValue: String) {
        val currentSettings = _uiState.value.settings
        updateSettings(currentSettings.copy(empresaSitef = newValue))
    }

    fun updateEnderecoSitef(newValue: String) {
        val currentSettings = _uiState.value.settings
        updateSettings(currentSettings.copy(enderecoSitef = newValue))
    }

    fun updateOperador(newValue: String) {
        val currentSettings = _uiState.value.settings
        updateSettings(currentSettings.copy(operador = newValue))
    }

    fun updateCnpjCpf(newValue: String) {
        val currentSettings = _uiState.value.settings
        updateSettings(currentSettings.copy(cnpjCpf = newValue))
    }

    fun updateTimeoutColeta(newValue: String) {
        val currentSettings = _uiState.value.settings
        updateSettings(currentSettings.copy(timeoutColeta = newValue))
    }

    fun toggleImprimirComprovantes(isChecked: Boolean) {
        FinApplication.printReceipt = isChecked
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