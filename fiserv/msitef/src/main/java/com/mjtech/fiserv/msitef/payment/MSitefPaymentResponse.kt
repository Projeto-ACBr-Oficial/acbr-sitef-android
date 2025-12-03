package com.mjtech.fiserv.msitef.payment

import android.content.Intent

data class MSitefPaymentResponse(
    val codResp: String?,
    val compDadosConf: String?,
    val codTrans: String?,
    val redeAut: String?,
    val bandeira: String?,
    val codAutorizacao: String?,
    val tipoParc: String?,
    val vlTroco: String?,
    val numParc: String?,
    val nsuSitef: String?,
    val nsuHost: String?,
    val viaEstabelecimento: String?,
    val viaCliente: String?,
    val tipoCampos: String?
) {
    /**
     * Construtor auxiliar que recebe a Intent (ou apenas o Bundle de dados) e inicializa
     * a data class, tratando todos os campos como Strings opcionais (String?).
     */
    constructor(data: Intent?) : this(
        codResp = data?.getStringExtra("CODRESP"),
        compDadosConf = data?.getStringExtra("COMP_DADOS_CONF"),
        codTrans = data?.getStringExtra("CODTRANS"),
        redeAut = data?.getStringExtra("REDE_AUT"),
        bandeira = data?.getStringExtra("BANDEIRA"),
        codAutorizacao = data?.getStringExtra("COD_AUTORIZACAO"),
        tipoParc = data?.getStringExtra("TIPO_PARC"),
        vlTroco = data?.getStringExtra("VLTROCO"),
        numParc = data?.getStringExtra("NUM_PARC"),
        nsuSitef = data?.getStringExtra("NSU_SITEF"),
        nsuHost = data?.getStringExtra("NSU_HOST"),
        viaEstabelecimento = data?.getStringExtra("VIA_ESTABELECIMENTO"),
        viaCliente = data?.getStringExtra("VIA_CLIENTE"),
        tipoCampos = data?.getStringExtra("TIPO_CAMPOS")
    )
}
