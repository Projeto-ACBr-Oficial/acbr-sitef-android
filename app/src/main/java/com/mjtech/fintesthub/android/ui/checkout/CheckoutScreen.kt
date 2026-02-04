package com.mjtech.fintesthub.android.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjtech.domain.common.Result
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.common.components.FinButton
import com.mjtech.fintesthub.android.ui.common.util.toCurrencyFormat
import com.mjtech.fintesthub.android.ui.theme.MainColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutScreen(
    paddingValues: PaddingValues,
    valueInCents: Long,
    onCancel: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setTransactionAmount(valueInCents)
    }

    uiState.transactionResult?.let { result ->
        TransactionResultDialog(
            result = result,
            onDismiss = {
                viewModel.resetTransactionResult()
                if (result.isSuccess) onCancel()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        HeaderDisplay(valueInCents.toCurrencyFormat())

        when {
            uiState.isLoading -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.selectedPaymentMethodId != null &&
                    uiState.installmentsOptions is Result.Success -> {
                val installmentOptions = (uiState.installmentsOptions as Result.Success).data

                InstallmentOptionContent(
                    installments = installmentOptions,
                    onInstallmentSelected = viewModel::onInstallmentSelected
                )
            }

            uiState.availableUiMethods.isNotEmpty() -> {
                PaymentMethodContent(
                    methods = uiState.availableUiMethods,
                    onMethodSelected = viewModel::onPaymentMethodSelected
                )
            }

            else -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f), contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.generic_error), color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        FinButton(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.cancel),
            onClick = onCancel
        )
    }
}

/**
 * [Composable] HeaderDisplay
 * ------------------------
 * Exibe o cabeçalho com o valor a ser pago.
 */
@Composable
fun HeaderDisplay(value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = MainColor.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.amount_to_pay),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                color = MainColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}