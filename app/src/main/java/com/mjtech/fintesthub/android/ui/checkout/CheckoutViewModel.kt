package com.mjtech.fintesthub.android.ui.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.common.Result
import com.mjtech.domain.payment.model.InstallmentDetails
import com.mjtech.domain.payment.model.InstallmentOption
import com.mjtech.domain.payment.model.InstallmentType
import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.model.PaymentType
import com.mjtech.domain.payment.repository.PaymentCallback
import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.payment.repository.PaymentRepository
import com.mjtech.domain.print.model.TextPrint
import com.mjtech.domain.print.model.TextStyle
import com.mjtech.domain.print.repository.PrintRepository
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fintesthub.android.data.settings.core.MainSettingsKeys.PRINT_RECEIPT
import com.mjtech.fintesthub.android.ui.checkout.models.TransactionResultUi
import com.mjtech.fintesthub.android.ui.common.mappers.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val paymentRepository: PaymentRepository,
    private val paymentProcessor: PaymentProcessor,
    private val printRepository: PrintRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadPaymentMethods()
        getPrintReceiptFlag()
    }

    private val paymentCallback = object : PaymentCallback {
        override fun onSuccess(transactionId: String, message: String?) {
            Log.d(TAG, "Payment successful: $transactionId, receipt: $message")

            if (uiState.value.isPrintEnabled) {
                printReceipt(message ?: "null")
            }

            _uiState.update {
                it.copy(
                    transactionResult = TransactionResultUi(
                        isSuccess = true,
                        title = "Sucesso!",
                        message = message ?: "Transação realizada com ID: $transactionId"
                    )
                )
            }
        }

        override fun onFailure(errorCode: String, errorMessage: String) {
            Log.e(TAG, "Payment failed: $errorCode, message: $errorMessage")
            _uiState.update {
                it.copy(
                    transactionResult = TransactionResultUi(
                        isSuccess = false,
                        title = "Falha na Transação",
                        message = "Erro [$errorCode]: $errorMessage"
                    )
                )
            }
        }

        override fun onCancelled(message: String?) {
            Log.e(TAG, "Payment cancelled: message: $message")
            _uiState.update {
                it.copy(
                    transactionResult = TransactionResultUi(
                        isSuccess = false,
                        title = "Cancelado",
                        message = message ?: "A operação foi interrompida pelo usuário."
                    )
                )
            }
        }
    }

    fun setTransactionAmount(amount: Long) {
        val total = amount / 100.0
        _uiState.update { it.copy(transactionAmount = total) }
    }

    fun onPaymentMethodSelected(methodId: String) {
        val selectedMethod = _uiState.value.availablePaymentMethods.find { it.id == methodId }
        val amount = _uiState.value.transactionAmount

        _uiState.update {
            it.copy(selectedPaymentMethodId = methodId)
        }

        val paymentType = try {
            PaymentType.valueOf(methodId)
        } catch (_: Exception) {
            Log.e(TAG, "Método de pagamento inválido selecionado: $methodId")
            return
        }


        val orderId = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                payment = Payment(
                    id = orderId,
                    amount = amount,
                    type = paymentType,
                    installmentDetails = null
                )
            )
        }

        if (selectedMethod?.requiresInstallments == true && amount > 0.0) {
            loadInstallmentOptions(methodId, amount)
        } else if (selectedMethod?.requiresInstallments == false && amount > 0.0) {
            processPayment()
        }
    }

    fun onInstallmentSelected(installment: InstallmentOption) {
        _uiState.update {
            it.copy(
                payment = it.payment?.copy(
                    installmentDetails = InstallmentDetails(
                        installments = installment.count,
                        installmentType = InstallmentType.MERCHANT
                    )
                )
            )
        }
        processPayment()
    }

    fun processPayment() {
        _uiState.value.apply {
            if (transactionAmount <= 0) {
                _uiState.update {
                    it.copy(
                        transactionResult = TransactionResultUi(
                            isSuccess = false,
                            title = "Erro",
                            message = "O valor da transação deve ser maior que zero."
                        )
                    )
                }
                return
            }

            val currentPayment = payment
            if (currentPayment == null) {
                _uiState.update {
                    it.copy(
                        transactionResult = TransactionResultUi(
                            isSuccess = false,
                            title = "Erro",
                            message = "Erro desconhecido."
                        )
                    )
                }
                return
            }

            paymentProcessor.processPayment(currentPayment, paymentCallback)
        }
    }

    fun resetTransactionResult() {
        _uiState.update { it.copy(transactionResult = null) }
    }

    private fun getPrintReceiptFlag() {
        _uiState.update {
            it.copy(
                isPrintEnabled = Settings.getValue(PRINT_RECEIPT, false)
            )
        }
    }

    private fun printReceipt(receipt: String) {
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

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            paymentRepository.getAvailablePaymentMethods()
                .collect { result ->
                    _uiState.update { currentState ->
                        val mappedMethods = (result as? Result.Success)?.data?.map { it.toUi() }

                        currentState.copy(
                            paymentMethods = result,
                            availableUiMethods = mappedMethods ?: emptyList(),
                            isLoading = result is Result.Loading
                        )
                    }
                }
        }
    }

    private fun loadInstallmentOptions(methodId: String, amount: Double) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    installmentsOptions = Result.Loading,
                    isLoading = true
                )
            }

            paymentRepository.getInstallmentOptions(methodId, amount)
                .collect { result ->
                    _uiState.update { currentState ->
                        val isLoadingMethods = currentState.paymentMethods is Result.Loading
                        currentState.copy(
                            installmentsOptions = result,
                            isLoading = isLoadingMethods || (result is Result.Loading)
                        )
                    }
                }
        }
    }


    companion object {
        private const val TAG = "CheckoutViewModel"
    }
}