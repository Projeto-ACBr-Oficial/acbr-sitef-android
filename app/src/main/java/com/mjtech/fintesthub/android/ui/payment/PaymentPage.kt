package com.mjtech.fintesthub.android.ui.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.theme.Typography

val MAX_KEYPAD_WIDTH = 400.dp


@Composable
fun PaymentPage(onNavigate: (Long) -> Unit) {
    val viewModel: PaymentViewModel = viewModel()

    val currentUiState = viewModel.uiState.value

    val (valueInCents, formattedValue, errorMessage) = currentUiState

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearError()
            },
            title = { Text(stringResource(R.string.alert)) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearError()
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .widthIn(max = MAX_KEYPAD_WIDTH)
        ) {
            DisplayField(value = formattedValue)

            NumericKeypad(
                onNumberClick = viewModel::appendNumber,
                onDeleteClick = viewModel::deleteLastDigit,
                onClearClick = viewModel::clearValue
            )

            ActionButton(
                label = stringResource(R.string.pay),
                color = Color(0xFF4CAF50)
            ) {
                val isPaymentValid = viewModel.onPaymentAction()
                if (isPaymentValid) {
                    onNavigate(valueInCents)
                    viewModel.clearValue()
                }
            }
        }
    }
}

/**
 * [Composable] DisplayField
 * ------------------------
 * Exibe o valor de pagamento atual.
 */
@Composable
fun DisplayField(value: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(54.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

/**
 * [Composable] NumericKeypad
 * --------------------------
 * Grade de botões numéricos e de ação.
 */
@Composable
fun NumericKeypad(
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "X", "0", "<"
    )
    Column(
        modifier = Modifier.fillMaxWidth(0.85f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        keys.chunked(3).forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowKeys.forEach { key ->
                    KeypadButton(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (key) {
                                "X" -> onClearClick()
                                "<" -> onDeleteClick()
                                else -> onNumberClick(key.toInt())
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * [Composable] KeypadButton
 * -------------------------
 * Botão individual do teclado.
 */
@Composable
fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonColor = when (text) {
        "X" -> Color(0xFFF44336)
        "<" -> Color(0xFFFFC107)
        else -> Color(0xFFE3F2FD)
    }
    val contentColor = if (text == "X" || text == "<") Color.White else Color.Black

    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .heightIn(min = 50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = null
    ) {
        Text(
            text = if (text == "<") "⌫" else text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * [Composable] ActionButton
 * -------------------------
 * Botão de ação na parte inferior.
 */
@Composable
fun ActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.medium,
        elevation = null
    ) {
        Text(text = label, style = Typography.headlineMedium.copy(fontSize = 18.sp))
    }
}


@Preview
@Composable
private fun PaymentPagePreview() {
    PaymentPage { }
}
