package com.mjtech.domain.settings.model

object Settings {

    private val _settingsMap = mutableMapOf<String, Any>()

    val settingsMap: Map<String, Any>
        get() = _settingsMap.toMap()

    /**
     * Atualiza o mapa com a nova configuração.
     * @param setting Um objeto Setting(key, value)
     */
    fun updateSetting(setting: Setting) {
        _settingsMap[setting.key] = setting.value
    }

    /**
     * Função auxiliar para recuperar um valor de qualquer lugar.
     * @param key A chave do valor
     * @param defaultValue O valor padrão caso a chave não exista
     */
    inline fun <reified T> getValue(key: String, defaultValue: T): T {
        return settingsMap[key] as? T ?: defaultValue
    }
}