package com.mjtech.fiserv.msitef.common

import com.mjtech.domain.settings.model.MSitefSettings

object MSiTefData {

    val data = MSitefSettings(
        empresaSitef = "00000000",
        enderecoSitef = "192.168.237.53;192.168.237.53:20036",
        operador = "0001",
        cnpjCpf = "18760540000139",
        cnpjAutomacao = "18760540000139",
        timeoutColeta = "60"
    )
}