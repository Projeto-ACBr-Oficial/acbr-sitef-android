package com.mjtech.fintesthub.android.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.ui.common.components.FinButton
import com.mjtech.fintesthub.android.ui.common.components.FinSwitch
import com.mjtech.fintesthub.android.ui.common.components.FinTextField
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingsPage(
    viewModel: SettingsViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
            return
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    item {
                        FinTextField(
                            value = "",
                            onValueChange = {},
                            label = "Empresa SiTef",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = "",
                            onValueChange = {},
                            label = "Endereço IP SiTef",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = "",
                            onValueChange = { },
                            label = "Operador",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = "",
                            onValueChange = {},
                            label = "CNPJ ou CPF",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = "",
                            onValueChange = {},
                            label = "Timeout (segundos)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }

                    item {
                        FinSwitch(
                            title = "Imprimir comprovantes",
                            subtitle = "Habilitar impressão automática de comprovantes",
                            isChecked = true,
                            onCheckedChange = {}
                        )
                    }
                }

                FinButton(
                    onClick = { },
                    text = stringResource(R.string.save_settings),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}