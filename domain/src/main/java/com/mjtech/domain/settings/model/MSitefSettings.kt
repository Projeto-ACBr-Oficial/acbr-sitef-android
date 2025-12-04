package com.mjtech.fiserv.msitef.settings

import com.mjtech.domain.settings.model.Settings

data class MSitefSettings(
    val empresaSitef: String = "00000000",
    val enderecoSitef: String = "127.0.0.1;127.0.0.1:20036",
    val operador: String = "0001",
    val cnpjCpf: String = "12345678912345",
    val timeoutColeta: String = "60",
): Settings
