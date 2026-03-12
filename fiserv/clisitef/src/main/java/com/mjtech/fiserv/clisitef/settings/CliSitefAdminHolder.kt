package com.mjtech.fiserv.clisitef.settings

import com.mjtech.domain.settings.repository.AdminMenuCallback

internal object CliSitefAdminHolder {

    var callback: AdminMenuCallback? = null

    fun initialize(callback: AdminMenuCallback) {
        this.callback = callback
    }

    fun clear() {
        this.callback = null
    }
}