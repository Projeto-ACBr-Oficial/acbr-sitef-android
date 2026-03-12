package com.mjtech.fiserv.clisitef.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mjtech.domain.settings.repository.AdminMenuCallback
import com.mjtech.domain.settings.repository.TefAdminAction

internal class CliSitefAdminHandler(private val context: Context) : TefAdminAction {


    override fun openAdminMenu(callback: AdminMenuCallback) {
        CliSitefAdminHolder.initialize(callback)

        val adminIntent = Intent(context, CliSitefAdminActivity::class.java).apply {
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        context.startActivity(adminIntent)
    }
}