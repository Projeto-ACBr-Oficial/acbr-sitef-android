# Módulo M-SiTef — Documentação Funcional

> **Versão:** 1.0 · **Data:** 2026-02-26  
> **Namespace:** `com.mjtech.fiserv.msitef`

---

## 1. O que é este módulo?

O módulo `:fiserv:msitef` é um **Android Library** que encapsula toda a lógica de integração com o aplicativo **M-SiTef** (Software Express / Fiserv) via `Intent` explícita.  
Ele expõe uma interface limpa (`PaymentProcessor`) para o restante da aplicação, mantendo qualquer acoplamento ao SDK do SiTef restrito a este módulo.

---

## 2. Arquitetura

```
:domain          ← interfaces e modelos puros (sem Android)
    └─ PaymentProcessor    (interface)
    └─ PaymentCallback     (interface)
    └─ Payment             (data class)
    └─ Settings            (objeto singleton de configuração)

:fiserv:msitef   ← implementação M-SiTef
    ├─ MSitefPaymentProcessor   ← implementa PaymentProcessor
    ├─ MSitefPaymentActivity    ← proxy de Activity (lança o M-SiTef e recebe o resultado)
    ├─ MSitefPaymentHolder      ← object singleton que passa callback/payment entre classes
    ├─ MSitefResponse           ← mapeia a Intent de retorno do M-SiTef
    ├─ MSitefSettingsKey        ← constantes de chave para Settings
    ├─ Utils.kt                 ← helpers de data/hora/valor/endereço
    └─ MSitefModule.kt          ← módulo Koin (injeção de dependência)
```

### Fluxo de uma transação

```
App (ViewModel)
  │
  ▼
PaymentProcessor.processPayment(payment, callback)
  │   (implementado por MSitefPaymentProcessor)
  │
  ├─ lê configurações de Settings (empresa, endereço, operador…)
  ├─ monta Intent para "br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF"
  ├─ salva callback + payment em MSitefPaymentHolder
  └─ abre MSitefPaymentActivity
        │
        └─ startActivityForResult(fiservIntent, 1001)
              │
              └─ App M-SiTef processa a transação
              │
              ◀─ onActivityResult(resultCode, data: Intent)
                    │
                    ├─ RESULT_OK + codResp == "0" → callback.onSuccess(id, viaEstabelecimento)
                    ├─ RESULT_OK + codResp != "0" → callback.onFailure(codResp, msg)
                    ├─ RESULT_CANCELED            → callback.onCancelled()
                    └─ outros                     → callback.onFailure("FISERV_ERROR", msg)
                    
              MSitefPaymentHolder.clear()  ← limpa estado ao final
```

---

## 3. Classes e responsabilidades

### 3.1 `MSitefPaymentProcessor`

**Visibilidade:** `internal` (só acessível dentro do módulo).  
**Injetado por:** Koin como `PaymentProcessor`.

| Responsabilidade | Detalhes |
|---|---|
| Mapear `PaymentType` → código de modalidade SiTef | `DEBIT=2`, `CREDIT=3`, `PIX=122`, `VOUCHER=2` |
| Mapear restrições | `DEBIT/VOUCHER=16`, `CREDIT_À_VISTA=26`, `CREDIT_PARC=27`, `PIX=7;8;3919` |
| Converter valor | `Double.toStringWithoutDots()` → ex: 10.50 → `"1050"` |
| Montar Intent SiTef | ação `br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF` |
| Abrir Activity proxy | `MSitefPaymentActivity` com a Intent SiTef embutida |

**Extras enviados ao M-SiTef:**

| Extra | Fonte | Exemplo |
|---|---|---|
| `empresaSitef` | `Settings[EMPRESA_SITEF]` | `"00000000"` |
| `enderecoSitef` | `Settings[ENDERECO_SITEF]` + `.getFullAddress()` | `"192.168.1.1;192.168.1.1:20036"` |
| `operador` | `Settings[OPERADOR]` | `"0001"` |
| `CNPJ_CPF` | `Settings[CNPJ_CPF]` | `"12345678912345"` |
| `cnpj_automacao` | `Settings[CNPJ_AUTOMACAO]` | `"12345678912345"` |
| `data` | `getCurrentDate()` | `"20260226"` |
| `hora` | `getCurrentTime()` | `"143022"` |
| `numeroCupom` | data + hora | `"20260226143022"` |
| `valor` | `payment.amount.toStringWithoutDots()` | `"1050"` |
| `modalidade` | `mapPaymentMethod(payment.type)` | `"3"` |
| `numParcelas` | `payment.installmentDetails?.installments` | `"3"` (apenas se parcelado) |
| `restricoes` | `mapRestriction(...)` | `"TransacoesHabilitadas=26"` |
| `timeoutColeta` | fixo | `"60"` |
| `comExterna` | fixo | `"0"` |

---

### 3.2 `MSitefPaymentActivity`

Activity **transparente** que serve de proxy entre o app e o M-SiTef.  
Declarada no `AndroidManifest.xml` do módulo com `android:exported="false"`.

**Funcionamento:**
1. Recebe a Intent do M-SiTef encapsulada no extra `"fiserv_intent"`.
2. Chama `startActivityForResult` com código `1001`.
3. Em `onActivityResult`, interpreta o resultado e delega para o `PaymentCallback` via `MSitefPaymentHolder`.
4. Chama `MSitefPaymentHolder.clear()` e `finish()`.

---

### 3.3 `MSitefPaymentHolder`

`internal object` — singleton de memória que resolve o problema de comunicação entre `MSitefPaymentProcessor` (que cria a Intent) e `MSitefPaymentActivity` (que recebe o resultado), já que não é possível passar um callback diretamente em uma Intent.

> ⚠️ **Importante:** o holder é limpo em `MSitefPaymentHolder.clear()` ao final de cada transação. Não inicie uma nova transação enquanto outra estiver em andamento.

---

### 3.4 `MSitefResponse`

Data class que desserializa todos os extras retornados pelo M-SiTef na Intent de resultado.

| Campo | Extra Intent | Descrição |
|---|---|---|
| `codResp` | `CODRESP` | `"0"` = sucesso |
| `compDadosConf` | `COMP_DADOS_CONF` | Dados de confirmação |
| `codTrans` | `CODTRANS` | Código da transação |
| `redeAut` | `REDE_AUT` | Rede autorizadora |
| `bandeira` | `BANDEIRA` | Bandeira do cartão |
| `codAutorizacao` | `COD_AUTORIZACAO` | Código de autorização |
| `tipoParc` | `TIPO_PARC` | Tipo de parcelamento |
| `vlTroco` | `VLTROCO` | Valor de troco |
| `numParc` | `NUM_PARC` | Número de parcelas |
| `nsuSitef` | `NSU_SITEF` | NSU do SiTef |
| `nsuHost` | `NSU_HOST` | NSU do host |
| `viaEstabelecimento` | `VIA_ESTABELECIMENTO` | Comprovante do estabelecimento |
| `viaCliente` | `VIA_CLIENTE` | Comprovante do cliente |
| `tipoCampos` | `TIPO_CAMPOS` | Tipo de campos retornados |

---

### 3.5 `MSitefSettingsKey`

Constantes de chave para leitura no objeto `Settings` do domínio.

| Constante | Valor | Descrição |
|---|---|---|
| `EMPRESA_SITEF` | `"EMPRESA_SITEF"` | Código da empresa no SiTef (8 dígitos) |
| `ENDERECO_SITEF` | `"ENDERECO_SITEF"` | IP do servidor SiTef |
| `OPERADOR` | `"OPERADOR"` | Código do operador/caixa |
| `CNPJ_CPF` | `"CNPJ_CPF"` | CNPJ/CPF do estabelecimento |
| `CNPJ_AUTOMACAO` | `"CNPJ_AUTOMACAO"` | CNPJ do software de automação |

---

### 3.6 `Utils.kt`

| Função | Retorno | Exemplo |
|---|---|---|
| `getCurrentDate()` | `String` `"yyyyMMdd"` | `"20260226"` |
| `getCurrentTime()` | `String` `"HHmmss"` | `"143022"` |
| `Double.toStringWithoutDots()` | `String` sem ponto decimal | `10.50 → "1050"` |
| `String.getFullAddress()` | `String` | `"192.168.1.1;192.168.1.1:20036"` |

---

## 4. Dependências do módulo

### 4.1 Módulos internos

```
:domain
```

### 4.2 Dependências externas (Gradle)

```kotlin
// build.gradle.kts do módulo :fiserv:msitef
implementation(project(":domain"))
implementation("io.insert-koin:koin-android:3.5.0")
implementation("androidx.core:core-ktx:1.17.0")
implementation("androidx.appcompat:appcompat:1.7.1")
implementation("com.google.android.material:material:1.13.0")
implementation("androidx.activity:activity:1.10.1")
implementation("androidx.constraintlayout:constraintlayout:2.2.1")
```

### 4.3 Dependência de runtime (não declarada em Gradle)

O módulo **não inclui** o aplicativo M-SiTef. Ele precisa estar **instalado no dispositivo** para que a Intent seja resolvida. O pacote resolvido pela ação é:

```
br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF
```

---

## 5. Configuração inicial (`Settings`)

Antes de processar qualquer pagamento, popule o objeto `Settings` com as configurações do SiTef. Isso deve ser feito na inicialização do app (ex: `Application.onCreate` ou após o usuário salvar as configurações):

```kotlin
import com.mjtech.domain.settings.model.Setting
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.*

Settings.updateSetting(Setting(EMPRESA_SITEF,    "00000000"))
Settings.updateSetting(Setting(ENDERECO_SITEF,   "192.168.1.100"))
Settings.updateSetting(Setting(OPERADOR,         "0001"))
Settings.updateSetting(Setting(CNPJ_CPF,         "12345678000195"))
Settings.updateSetting(Setting(CNPJ_AUTOMACAO,   "12345678000195"))
```

---

## 6. Como usar no seu app (com Koin)

### 6.1 `settings.gradle.kts`

```kotlin
include(":domain")
include(":fiserv:msitef")
```

### 6.2 `build.gradle.kts` do app

```kotlin
dependencies {
    implementation(project(":domain"))
    implementation(project(":fiserv:msitef"))
    implementation("io.insert-koin:koin-android:3.5.0")
}
```

### 6.3 `Application.kt`

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Iniciar Koin com o módulo do MSitef
        startKoin {
            androidContext(this@MyApplication)
            modules(
                myAppModule,
                msitefModule   // importar de com.mjtech.fiserv.msitef.di
            )
        }

        // 2. Popular configurações do SiTef
        Settings.updateSetting(Setting(EMPRESA_SITEF,  "00000000"))
        Settings.updateSetting(Setting(ENDERECO_SITEF, "192.168.1.100"))
        Settings.updateSetting(Setting(OPERADOR,       "0001"))
        Settings.updateSetting(Setting(CNPJ_CPF,       "12345678000195"))
        Settings.updateSetting(Setting(CNPJ_AUTOMACAO, "12345678000195"))
    }
}
```

### 6.4 Processar pagamento no ViewModel

```kotlin
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor   // injetado pelo Koin
) : ViewModel() {

    fun pay(amount: Double) {
        val payment = Payment(
            id = System.currentTimeMillis(),
            amount = amount,
            type = PaymentType.CREDIT,
            installmentDetails = null
        )

        paymentProcessor.processPayment(payment, object : PaymentCallback {
            override fun onSuccess(transactionId: String, message: String?) {
                // atualizar UI
            }
            override fun onFailure(errorCode: String, errorMessage: String) {
                // tratar erro
            }
            override fun onCancelled(message: String?) {
                // transação cancelada pelo usuário
            }
        })
    }
}
```

---

## 7. Modelos do domínio

### `Payment`
```kotlin
data class Payment(
    val id: Long,
    val amount: Double,           // ex: 10.50
    val type: PaymentType,
    val installmentDetails: InstallmentDetails?
)
```

### `PaymentType`
```kotlin
enum class PaymentType { DEBIT, CREDIT, PIX, VOUCHER, INSTANT_PAYMENT }
```

### `InstallmentDetails`
```kotlin
data class InstallmentDetails(
    val installments: Int,          // número de parcelas
    val installmentType: InstallmentType  // NONE | MERCHANT | ISSUER
)
```

### `PaymentCallback`
```kotlin
interface PaymentCallback {
    fun onSuccess(transactionId: String, message: String? = null)
    fun onFailure(errorCode: String, errorMessage: String)
    fun onCancelled(message: String? = null)
}
```

---

## 8. Códigos de erro conhecidos

| Código | Origem | Descrição |
|---|---|---|
| `"0"` | M-SiTef | **Sucesso** |
| Qualquer outro `codResp` | M-SiTef | Erro específico do SiTef |
| `INTEGRATION_ERROR` | Módulo | Exceção ao abrir o M-SiTef (app não instalado, etc.) |
| `INVALID_DATA` | Módulo | Intent de pagamento nula |
| `FISERV_ERROR` | Módulo | `resultCode` inesperado |
| `UNKNOWN_ERROR` | Módulo | `codResp` nulo na resposta |
