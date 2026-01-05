package com.mjtech.fiserv.msitef.settings

import com.mjtech.domain.settings.repository.AdminMenuCallback

internal object MSitefAdminHolder {

    var callback: AdminMenuCallback? = null

    fun initialize(callback: AdminMenuCallback) {
        this.callback = callback
    }

    fun clear() {
        this.callback = null
    }
}