package com.mjtech.fintesthub.android.ui.payment

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mjtech.fintesthub.android.ui.common.util.toCurrencyFormat

class PaymentViewModel : ViewModel() {

    private val _uiState = mutableStateOf(PaymentUiState())

    val uiState: State<PaymentUiState> = _uiState


    /**
     * [appendNumber]
     * Adiciona um dígito ao valor de pagamento.
     * @param number: O dígito (0 a 9) clicado.
     */
    fun appendNumber(number: Int) {
        val currentCents = uiState.value.valueInCents
        if (currentCents >= 999999999L) return
        val newCents = currentCents * 10 + number
        updateState(newCents)
    }

    /**
     * [deleteLastDigit]
     * Remove o último dígito do valor de pagamento.
     */
    fun deleteLastDigit() {
        val currentCents = uiState.value.valueInCents
        val newCents = currentCents / 10
        updateState(newCents)
    }

    /**
     * [clearValue]
     * Limpa completamente o valor.
     */
    fun clearValue() {
        updateState(0L)
    }

    /**
     * [onPaymentAction]
     * Executa a validação para pagamento.
     * @return Boolean: Retorna true se o valor for válido (> 0), false caso contrário.
     */
    fun onPaymentAction(): Boolean {
        if (uiState.value.valueInCents > 0) {
            updateState(uiState.value.valueInCents, null)
            return true
        } else {
            updateState(
                newCents = uiState.value.valueInCents,
                errorMessage = "O valor para pagamento deve ser superior a R$ 0,00 para continuar."
            )
            return false
        }
    }

    /**
     * [clearError]
     * Limpa a mensagem de erro atual.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun updateState(newCents: Long, errorMessage: String? = null) {
        _uiState.value = _uiState.value.copy(
            valueInCents = newCents,
            formattedValue = newCents.toCurrencyFormat(),
            errorMessage = errorMessage
        )
    }
}