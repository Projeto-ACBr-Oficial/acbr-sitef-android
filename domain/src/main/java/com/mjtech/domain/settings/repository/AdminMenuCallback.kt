package com.mjtech.domain.settings.repository

interface AdminMenuCallback {
    fun onSuccess(receipt: String? = null)
    fun onFailure(errorCode: String, errorMessage: String)
}