package com.mjtech.fintesthub.android.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.checkout.models.PaymentMethodUi
import com.mjtech.fintesthub.android.ui.theme.Gray300
import com.mjtech.fintesthub.android.ui.theme.Gray600
import com.mjtech.fintesthub.android.ui.theme.Typography

@Composable
fun PaymentMethodContent(
    methods: List<PaymentMethodUi>,
    onMethodSelected: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.select_payment_method),
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
        items(methods) { method ->
            PaymentMethodItem(
                method = method,
                onClick = { onMethodSelected(method.id) }
            )
        }
    }
}

/**
 * [Composable] PaymentMethodItem
 * ------------------------
 * Representa um item de método de pagamento na lista.
 */
@Composable
fun PaymentMethodItem(
    method: PaymentMethodUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(method.icon),
            contentDescription = method.name,
            tint = method.color.copy(alpha = 0.4f),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = method.name,
                style = Typography.headlineMedium,
            )
            Text(
                text = method.description,
                style = Typography.labelMedium,
                color = Gray600
            )
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