package com.mjtech.domain.settings.model

data class MSitefSettings(
    var empresaSitef: String,
    var enderecoSitef: String,
    var operador: String,
    var cnpjCpf: String,
    var cnpjAutomacao: String,
    var timeoutColeta: String,
): Settings()
