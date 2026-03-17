package com.mjtech.fiserv.clisitef.settings

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.softwareexpress.sitef.android.CliSiTef
import br.com.softwareexpress.sitef.android.ICliSiTefListener
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fiserv.base.SitefSettingsKey.EMPRESA_SITEF
import com.mjtech.fiserv.base.SitefSettingsKey.ENDERECO_SITEF
import com.mjtech.fiserv.base.SitefSettingsKey.OPERADOR
import com.mjtech.fiserv.base.getCurrentDate
import com.mjtech.fiserv.base.getCurrentTime
import com.mjtech.fiserv.clisitef.R

internal class CliSitefAdminActivity : AppCompatActivity(), ICliSiTefListener {

    private lateinit var clisitef: CliSiTef

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clisitef_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (initClisitef() == 0) {
            openAdmin()
        } else {
            // TODO
        }

    }

    private fun initClisitef(): Int {
        clisitef = CliSiTef(this.applicationContext)
        clisitef.setActivity(this)

        val enderecoSitef = Settings.getValue(ENDERECO_SITEF, "")
        val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")

        // O número do terminal deve conter 8 dígitos
        val numeroTerminal = "01234560"

        val configResult = clisitef.configure(enderecoSitef, empresaSitef, numeroTerminal, null)

        Log.d(TAG, "Configuração do CliSiTef retornou $configResult")

        return configResult
    }

    private fun openAdmin() {
        val operador = Settings.getValue(OPERADOR, "")

        val retorno =
            clisitef.startTransaction(
                this,
                110,
                "0",
                "",
                getCurrentDate(),
                getCurrentTime(),
                operador,
                null
            )
        Log.d(TAG, "openAdmin: $retorno")
    }

    override fun onData(
        p0: Int,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: Int,
        p5: ByteArray?
    ) {
        // TODO ("Not yet implemented")
    }

    override fun onTransactionResult(p0: Int, p1: Int) {
        // TODO ("Not yet implemented")
    }

    companion object {

        private val TAG = "CliSitefAdminActivity"
    }
}