package com.mjtech.fiserv.msitef.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjtech.fiserv.msitef.R
import com.mjtech.fiserv.msitef.databinding.ActivityMsitefPaymentBinding

class MSitefPaymentActivity : AppCompatActivity() {

    private val FISERV_REQUEST_CODE = 1001

    private lateinit var binding: ActivityMsitefPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMsitefPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fiservIntent: Intent? = intent.getParcelableExtra(FISERV_INTENT_KEY)

        openSitef(fiservIntent)
    }

    private fun openSitef(intent: Intent?) {
        if (intent != null) {
            try {
                startActivityForResult(intent, FISERV_REQUEST_CODE)
            } catch (e: Exception) {
                MSitefPaymentHolder.callback?.onFailure(
                    "INTEGRATION_ERROR",
                    "Falha ao iniciar o processador de pagamento: ${e.message}"
                )
                finish()
            }
        } else {
            MSitefPaymentHolder.callback?.onFailure(
                "INVALID_DATA",
                "Intent de pagamento não encontrada."
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

        if (requestCode == FISERV_REQUEST_CODE) {
            val callback = MSitefPaymentHolder.callback

            val response = MSitefPaymentResponse(data)
            Log.d(TAG, "Payment Response: $response")

            when (resultCode) {
                RESULT_OK -> {

                    // Verifica o código de resposta para determinar o sucesso ou falha do pagamento
                    if (response.codResp == "0") {
                        callback?.onSuccess(
                            MSitefPaymentHolder.payment?.id.toString(),
                            "Pagamento concluído."
                        )
                    } else {
                        callback?.onFailure(
                            response.codResp ?: "UNKNOWN_ERROR",
                            "Erro no pagamento."
                        )
                    }
                }

                RESULT_CANCELED -> {
                    callback?.onCancelled("Pagamento cancelado.")
                }

                else -> {
                    callback?.onFailure("FISERV_ERROR", "Erro desconhecido.")
                }
            }

            MSitefPaymentHolder.clear()
            finish()
        }
    }


    companion object {
        const val TAG = "MSitefPaymentActivity"
        const val FISERV_INTENT_KEY = "fiserv_intent"
    }
}