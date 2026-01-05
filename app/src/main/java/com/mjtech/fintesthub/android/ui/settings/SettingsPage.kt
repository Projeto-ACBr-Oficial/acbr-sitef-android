package com.mjtech.fintesthub.android.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjtech.fintesthub.android.R
import com.mjtech.fintesthub.android.data.settings.core.MainSettingsKeys
import com.mjtech.fintesthub.android.ui.common.components.FinButton
import com.mjtech.fintesthub.android.ui.common.components.FinSwitch
import com.mjtech.fintesthub.android.ui.common.components.FinTextField
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey
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
                val empresaSitef =
                    uiState.editableSettings[MSitefSettingsKey.EMPRESA_SITEF] as? String
                        ?: ""
                val enderecoSitef =
                    uiState.editableSettings[MSitefSettingsKey.ENDERECO_SITEF] as? String
                        ?: ""
                val operador =
                    uiState.editableSettings[MSitefSettingsKey.OPERADOR] as? String
                        ?: ""
                val cnpjCpf =
                    uiState.editableSettings[MSitefSettingsKey.CNPJ_CPF] as? String
                        ?: ""
                val cnpjAutomacao =
                    uiState.editableSettings[MSitefSettingsKey.CNPJ_AUTOMACAO] as? String
                        ?: ""
                val isPrintingEnabled =
                    uiState.editableSettings[MainSettingsKeys.PRINT_RECEIPT] as? Boolean
                        ?: false

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {


                    item {
                        FinTextField(
                            value = empresaSitef,
                            onValueChange = viewModel::updateEmpresaSitef,
                            label = "Empresa SiTef",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = enderecoSitef,
                            onValueChange = viewModel::updateEnderecoSitef,
                            label = "Endereço IP do SitDemo",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = operador,
                            onValueChange = viewModel::updateOperador,
                            label = "Operador",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = cnpjCpf,
                            onValueChange = viewModel::updateCnpjCpf,
                            label = "CNPJ ou CPF",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinTextField(
                            value = cnpjAutomacao,
                            onValueChange = viewModel::updateCnpjAutomacao,
                            label = "CNPJ da Automação",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        FinSwitch(
                            title = "Imprimir comprovantes",
                            subtitle = "Habilitar impressão automática de comprovantes",
                            isChecked = isPrintingEnabled,
                            onCheckedChange = viewModel::onPrintReceiptToggle
                        )
                    }
                }

                FinButton(
                    text = stringResource(R.string.menu_administrativo),
                    onClick = viewModel::openTefAdminMenu
                )
            }
        }
    }
}