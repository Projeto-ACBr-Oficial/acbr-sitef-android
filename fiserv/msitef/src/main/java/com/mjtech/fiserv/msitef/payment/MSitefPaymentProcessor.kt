package com.mjtech.fiserv.msitef.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.model.PaymentType
import com.mjtech.domain.payment.repository.PaymentCallback
import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.CNPJ_AUTOMACAO
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.CNPJ_CPF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.EMPRESA_SITEF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.ENDERECO_SITEF
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.OPERADOR
import com.mjtech.fiserv.msitef.common.getCurrentDate
import com.mjtech.fiserv.msitef.common.getCurrentTime
import com.mjtech.fiserv.msitef.common.getFullAddress
import com.mjtech.fiserv.msitef.common.toStringWithoutDots

internal class MSitefPaymentProcessor(private val context: Context) : PaymentProcessor {

    override fun processPayment(
        payment: Payment,
        callback: PaymentCallback
    ) {
        val modalidade = mapPaymentMethod(payment.type)
        val valor = payment.amount.toStringWithoutDots()
        val restricoes = mapRestriction(payment.type)

        val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")
        val enderecoSitef = Settings.getValue(ENDERECO_SITEF, "")
        val operador = Settings.getValue(OPERADOR, "")
        val cnpjCpf = Settings.getValue(CNPJ_CPF, "")
        val cnpjAutomacao = Settings.getValue(CNPJ_AUTOMACAO, "")

        Log.d(TAG, payment.toString())

        val fiservIntent = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF").apply {


            // Parâmetros de entrada para o SiTef
            putExtra("empresaSitef", empresaSitef)
            putExtra("enderecoSitef", enderecoSitef.getFullAddress())
            putExtra("operador", operador)
            putExtra("CNPJ_CPF", cnpjCpf)
            putExtra("cnpj_automacao", cnpjAutomacao)

            // Dados da transação
            putExtra("data", getCurrentDate())
            putExtra("hora", getCurrentTime())
            putExtra("numeroCupom", getCurrentDate() + getCurrentTime())

            putExtra("valor", valor)
            putExtra("modalidade", modalidade)

            // Verifica se há parcelamento
            if (payment.installmentDetails != null) {
                putExtra("numParcelas", payment.installmentDetails?.installments.toString())
            }

            if (payment.type != PaymentType.CREDIT) {
                putExtra("restricoes", restricoes)
            }

            // Configurações adicionais
            putExtra("timeoutColeta", "60")
            putExtra("comExterna", "0")

            Log.d(TAG, "Intent extras: ${this.extras}")
        }

        MSitefPaymentHolder.initialize(callback, payment)

        val paymentIntent = Intent(context, MSitefPaymentActivity::class.java).apply {
            putExtra(MSitefPaymentActivity.FISERV_INTENT_KEY, fiservIntent)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        context.startActivity(paymentIntent)
    }

    /** Mapeia o tipo de pagamento para o código esperado pelo SiTef */
    private fun mapPaymentMethod(method: PaymentType): String {
        return when (method) {
            PaymentType.DEBIT -> "2"
            PaymentType.CREDIT -> "3"
            PaymentType.PIX -> "122"
            PaymentType.VOUCHER -> "2"
            else -> "0"
        }
    }

    /** Mapeia as restrições de transação com base no tipo de pagamento */
    private fun mapRestriction(method: PaymentType): String {
        val restrictionType = "TransacoesHabilitadas"

        val restrictionCode = when (method) {
            PaymentType.DEBIT -> "16"
            PaymentType.VOUCHER -> "16"
            PaymentType.PIX -> "7;8;3919"
            else -> "0"
        }
        return "$restrictionType=$restrictionCode"
    }

    companion object {
        const val TAG = "MSitefPaymentProcessor"
    }
}