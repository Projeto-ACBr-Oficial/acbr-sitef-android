package com.mjtech.fintesthub.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.print.model.TextPrint
import com.mjtech.domain.print.model.TextStyle
import com.mjtech.domain.print.repository.PrintRepository
import com.mjtech.domain.settings.model.MSitefSettings
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import com.mjtech.fintesthub.android.FinApplication
import com.mjtech.fiserv.msitef.common.MSiTefData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val printRepository: PrintRepository
) : ViewModel() {

    val settings: MSitefSettings = MSiTefData.data

    private val _uiState = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    fun updateEmpresaSitef(newValue: String) {
        settings.empresaSitef = newValue

    }

    fun updateEnderecoSitef(newValue: String) {
        settings.enderecoSitef = newValue

    }

    fun updateOperador(newValue: String) {
       settings.operador = newValue
    }

    fun updateCnpjCpf(newValue: String) {
        settings.cnpjCpf = newValue
    }

    fun updateTimeoutColeta(newValue: String) {
        settings.timeoutColeta = newValue
    }

    fun toggleImprimirComprovantes(isChecked: Boolean) {
        FinApplication.printReceipt = isChecked
    }

    fun printReceipt(receipt: String) {
        viewModelScope.launch {
            printRepository.printSimpleText(TextPrint(receipt, TextStyle(""))).collect { result ->
                _uiState.update {
                    it.copy(
                        printResult = result
                    )
                }
            }
        }
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