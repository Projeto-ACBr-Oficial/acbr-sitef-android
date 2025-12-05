package com.mjtech.domain.settings.model

data class MSitefSettings(
    val empresaSitef: String = "00000000",
    val enderecoSitef: String = "127.0.0.1;127.0.0.1:20036",
    val operador: String = "0001",
    val cnpjCpf: String = "12345678912345",
    val cnpjAutomacao: String = "12345678912345",
    val timeoutColeta: String = "60",
): Settings()
