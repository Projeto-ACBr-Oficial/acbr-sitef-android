package com.mjtech.fintesthub.android.ui.configuration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mjtech.fintesthub.android.ui.theme.Typography


@Composable
fun ConfigurationPage() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Implemente as configurações necessária para sua integração aqui",
            style = Typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}