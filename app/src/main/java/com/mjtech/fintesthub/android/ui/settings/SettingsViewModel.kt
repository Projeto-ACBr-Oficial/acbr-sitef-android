package com.mjtech.fintesthub.android.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.common.Result
import com.mjtech.domain.print.model.TextPrint
import com.mjtech.domain.print.model.TextStyle
import com.mjtech.domain.print.repository.PrintRepository
import com.mjtech.domain.settings.model.Setting
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.SettingsRepository
import com.mjtech.fintesthub.android.data.settings.core.MainSettingsKeys
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias EditableSettings = Map<String, Any>

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val printRepository: PrintRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadEditableSettings()
    }

    fun updateEmpresaSitef(newValue: String) {
        val key = MSitefSettingsKey.EMPRESA_SITEF

        updateSettingValue(key, newValue)

        saveSetting(key, newValue)
    }

    fun updateEnderecoSitef(newValue: String) {
        val key = MSitefSettingsKey.ENDERECO_SITEF

        updateSettingValue(key, newValue)

        saveSetting(key, newValue)
    }

    fun updateOperador(newValue: String) {
        val key = MSitefSettingsKey.OPERADOR

        updateSettingValue(key, newValue)

        saveSetting(key, newValue)
    }

    fun updateCnpjCpf(newValue: String) {
        val key = MSitefSettingsKey.CNPJ_CPF

        updateSettingValue(key, newValue)

        saveSetting(key, newValue)
    }

    fun updateCnpjAutomacao(newValue: String) {
        val key = MSitefSettingsKey.CNPJ_AUTOMACAO

        updateSettingValue(key, newValue)

        saveSetting(key, newValue)
    }

    fun onPrintReceiptToggle(isChecked: Boolean) {
        val key = MainSettingsKeys.PRINT_RECEIPT

        updateSettingValue(key, isChecked)

        saveSetting(key, isChecked)
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

    private fun updateSettingValue(key: String, newValue: Any) {
        _uiState.update { currentState ->
            val newMap = currentState.editableSettings.toMutableMap()
            newMap[key] = newValue

            currentState.copy(editableSettings = newMap)
        }
    }

    private fun saveSetting(key: String, newValue: Any) {
        val settingToSave = Setting(key, newValue)

        viewModelScope.launch {
            settingsRepository.saveSetting(settingToSave).collect { result ->
                if (result is Result.Success) {
                    Settings.updateSetting(settingToSave)
                } else if (result is Result.Error) {
                    Log.e(TAG, result.error)
                }
            }
        }
    }

    private fun loadEditableSettings() {
        _uiState.update { currentState ->
            currentState.copy(
                editableSettings = Settings.settingsMap,
                isLoading = false
            )
        }
    }

    companion object {
        const val TAG = "SettingsViewModel"
    }
}