package com.mjtech.fiserv.msitef.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mjtech.domain.settings.model.Settings
import com.mjtech.domain.settings.repository.AdminMenuCallback
import com.mjtech.domain.settings.repository.TefAdminAction
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.CNPJ_AUTOMACAO
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.CNPJ_CPF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.EMPRESA_SITEF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.ENDERECO_SITEF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.OPERADOR

internal class MSitefAdminHandler(private val context: Context) : TefAdminAction {

    override fun openAdminMenu(callback: AdminMenuCallback) {

        val modalidade = "110" // Menu Administrativo

        val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")
        val enderecoSitef = Settings.getValue(ENDERECO_SITEF, "")
        val operador = Settings.getValue(OPERADOR, "")
        val cnpjCpf = Settings.getValue(CNPJ_CPF, "")
        val cnpjAutomacao = Settings.getValue(CNPJ_AUTOMACAO, "")
        val valor = "0"
        val restricoes = "TransacoesAdicionaisHabilitadas=8;3919"

        val intent = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF").apply {

            // Parâmetros de entrada para o SiTef
            putExtra("empresaSitef", empresaSitef)
            putExtra("enderecoSitef", enderecoSitef)
            putExtra("operador", operador)
            putExtra("CNPJ_CPF", cnpjCpf)
            putExtra("cnpj_automacao", cnpjAutomacao)

            // Configurações para o menu administrativo
            putExtra("modalidade", modalidade)
            putExtra("valor", valor)
            putExtra("restricoes", restricoes)
        }

        MSitefAdminHolder.initialize(callback)

        val adminIntent = Intent(context, MSitefAdminActivity::class.java).apply {
            putExtra(MSitefAdminActivity.FISERV_ADMIN_INTENT_KEY, intent)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        context.startActivity(adminIntent)
    }
}