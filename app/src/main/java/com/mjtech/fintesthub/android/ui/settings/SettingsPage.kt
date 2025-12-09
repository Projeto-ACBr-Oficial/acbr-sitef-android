package com.mjtech.fintesthub.android.ui.settings

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.mjtech.fiserv.msitef.common.getFullAddress
import com.mjtech.fiserv.msitef.payment.MSitefResponse
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsPage(
    viewModel: SettingsViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isPrintingEnabled =
        uiState.editableSettings[MainSettingsKeys.PRINT_RECEIPT] as? Boolean
            ?: false

    val adminMenuLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->

            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data: Intent? = result.data
                    val response = MSitefResponse(data)

                    if (isPrintingEnabled) {
                        viewModel.printReceipt(response.viaCliente.toString())
                    }
                }

                Activity.RESULT_CANCELED -> {
                    Log.d("SettingsPage", "Retorno do SiTef - Operação cancelada.")
                }

                else -> {
                    Log.e("SettingsPage", "Retorno do SiTef - Erro: ${result.resultCode}")
                }
            }
        }
    )

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
                    text = stringResource(R.string.menu_administrativo)
                ) {
                    val intent =
                        Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF").apply {
                            putExtra("modalidade", "110")
                            putExtra("empresaSitef", empresaSitef)
                            putExtra("enderecoSitef", enderecoSitef.getFullAddress())
                            putExtra("operador", operador)
                            putExtra("CNPJ_CPF", cnpjCpf)
                            putExtra("cnpj_automacao", cnpjAutomacao)
                            putExtra("valor", "0")
                            putExtra("restricoes", "TransacoesAdicionaisHabilitadas=8;3919")
                        }

                    try {
                        adminMenuLauncher.launch(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}