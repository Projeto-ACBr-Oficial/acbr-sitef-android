package com.mjtech.fintesthub.android.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mjtech.domain.payment.model.InstallmentOption
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.common.util.toCurrencyFormat
import com.mjtech.fintesthub.android.ui.theme.Gray300
import com.mjtech.fintesthub.android.ui.theme.Gray600
import com.mjtech.fintesthub.android.ui.theme.MainColor
import com.mjtech.fintesthub.android.ui.theme.Typography

@Composable
fun InstallmentOptionContent(
    installments: List<InstallmentOption>,
    onInstallmentSelected: (InstallmentOption) -> Unit
) {
    Text(
        text = stringResource(R.string.select_installment_option),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = Gray300
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(installments) { option ->
            InstallmentItem(
                option = option,
                onClick = { onInstallmentSelected(option) }
            )
        }
    }
}

/**
 * [Composable] InstallmentItem
 * ------------------------
 * Representa um item de opção de parcelamento na lista.
 */
@Composable
fun InstallmentItem(
    option: InstallmentOption,
    onClick: () -> Unit
) {
    val monthlyValue = option.monthlyAmount.toCurrencyFormat()
    val totalValue = option.totalAmount.toCurrencyFormat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.installment_option_x, option.count),
            style = Typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MainColor
            ),
            modifier = Modifier
                .width(32.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.installment_monthly_value, monthlyValue),
                style = Typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (option.interestRate > 0.0) {
                Text(
                    text = stringResource(R.string.total_a_pagar_com_taxa, totalValue),
                    style = Typography.labelMedium,
                    color = Color.Red.copy(alpha = 0.8f)
                )
            } else {
                Text(
                    text = stringResource(R.string.total_sem_juros, totalValue),
                    style = Typography.labelMedium,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Gray600
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 64.dp),
        thickness = 0.5.dp,
        color = Gray300
    )
}