package com.mjtech.fiserv.msitef.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjtech.fiserv.msitef.R
import com.mjtech.fiserv.msitef.common.MSitefResponse
import com.mjtech.fiserv.msitef.databinding.ActivityMsitefAdminBinding

class MSitefAdminActivity : AppCompatActivity() {

    private val FISERV_ADMIN_REQUEST_CODE = 1002

    private lateinit var binding: ActivityMsitefAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMsitefAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adminIntent: Intent? = intent.getParcelableExtra(FISERV_ADMIN_INTENT_KEY)

        openAdminMenu(adminIntent)
    }

    private fun openAdminMenu(intent: Intent?) {
        if (intent != null) {
            try {
                startActivityForResult(intent, FISERV_ADMIN_REQUEST_CODE)
            } catch (e: Exception) {
                MSitefAdminHolder.callback?.onFailure(
                    "INTEGRATION_ERROR",
                    "Falha ao iniciar o menu administrativo: ${e.message}"
                )
                finish()
            }
        } else {
            MSitefAdminHolder.callback?.onFailure(
                "INVALID_DATA",
                "Intent de menu não encontrada."
            )
            finish()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FISERV_ADMIN_REQUEST_CODE) {
            val callback = MSitefAdminHolder.callback

            val response = MSitefResponse(data)
            Log.d(TAG, "Admin Response: $response")

            when (resultCode) {
                RESULT_OK -> {
                    callback?.onSuccess(response.viaCliente)
                }

                RESULT_CANCELED -> {
                    callback?.onFailure(
                        "ADMIN_CANCELLED",
                        "Operação administrativa cancelada pelo usuário."
                    )
                }

                else -> {
                    callback?.onFailure("FISERV_ERROR", "Erro desconhecido.")
                }
            }

            MSitefAdminHolder.clear()
            finish()
        }
    }

    companion object {

        const val TAG = "MSitefAdminActivity"
        const val FISERV_ADMIN_INTENT_KEY = "FISERV_ADMIN_INTENT_KEY"
    }
}