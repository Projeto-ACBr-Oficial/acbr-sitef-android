package com.mjtech.fintesthub.android.ui.checkout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.mjtech.fintesthub.android.ui.checkout.models.TransactionResultUi
import com.mjtech.fintesthub.android.ui.common.components.FinButton

@Composable
fun TransactionResultDialog(
    result: TransactionResultUi,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FinButton(
                text = "OK",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        title = {
            Text(
                text = result.title,
                color = if (result.isSuccess) Color(0xFF2E7D32) else Color.Red,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(text = result.message)
        }
    )
}