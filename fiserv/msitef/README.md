# Módulo M-SiTef — Documentação Completa

> **Versão:** 1.0 · **Data:** 2026-03-12  
> **Namespace:** `com.mjtech.fiserv.msitef`

---

## 📋 Índice

1. [O que é este módulo?](#1-o-que-é-este-módulo)
2. [Arquitetura](#2-arquitetura)
3. [Fluxo de Uma Transação](#3-fluxo-de-uma-transação)
4. [Classes e Responsabilidades](#4-classes-e-responsabilidades)
5. [Dependências do Módulo](#5-dependências-do-módulo)
6. [Configuração Inicial (Settings)](#6-configuração-inicial-settings)
7. [Como Usar no Seu App (com Koin) — Padrão Recomendado](#7-como-usar-no-seu-app-com-koin--padrão-recomendado)
8. [Modelos do Domínio](#8-modelos-do-domínio)
9. [Códigos de Erro Conhecidos](#9-códigos-de-erro-conhecidos)
10. [Integração em Apps com Hilt](#10-integração-em-apps-com-hilt)
11. [Perguntas Frequentes](#11-perguntas-frequentes)

---

## 1. O que é este módulo?

O módulo `:fiserv:msitef` é um **Android Library** que encapsula toda a lógica de integração com o aplicativo **M-SiTef** (Software Express / Fiserv) via `Intent` explícita.

### Características Principais

- ✅ **Encapsulamento completo** do SDK M-SiTef (Fiserv)
- ✅ **Interface limpa** (`PaymentProcessor`) para o restante da aplicação
- ✅ **Zero acoplamento** ao código do app (usa Dependency Injection)
- ✅ **Gerenciamento automático** de callbacks e resultados
- ✅ **Tratamento de erros** padronizado
- ✅ **Suporte a Koin** (injeção de dependência recomendada)
- ✅ **Compatível com Hilt** (opção alternativa)

### Vantagem Arquitetural

```
┌─────────────────────────────────────────────────────┐
│  APP MODULE                                         │
│  ├─ UI (Activities, ViewModels)                    │
│  └─ depende de → DOMAIN (PaymentProcessor)        │
└─────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────┐
│  DOMAIN MODULE (Pure Kotlin)                        │
│  └─ Interfaces (PaymentProcessor, PaymentCallback) │
└─────────────────────────────────────────────────────┘
              ↑
┌─────────────────────────────────────────────────────┐
│  MSITEF MODULE (Android Library)                    │
│  ├─ Implementa → PaymentProcessor                   │
│  ├─ Activity Proxy → MSitefPaymentActivity          │
│  ├─ Holder → MSitefPaymentHolder                    │
│  └─ Resposta → MSitefResponse                       │
└─────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────┐
│  FISERV M-SITEF (App nativo, instalado no device)  │
│  └─ Processa transações de pagamento               │
└─────────────────────────────────────────────────────┘
```

**Benefício:** Se você quiser trocar de integração (ex: PagSeguro, Vivo, etc.), basta criar um novo módulo que implemente `PaymentProcessor`. O app não precisa mudar nada!

---

## 2. Arquitetura

```
:domain          ← interfaces e modelos puros (sem Android)
    └─ PaymentProcessor    (interface)
    └─ PaymentCallback     (interface)
    └─ Payment             (data class)
    └─ Settings            (objeto singleton de configuração)

:fiserv:msitef   ← implementação M-SiTef (Android Library)
    ├─ MSitefPaymentProcessor   ← implementa PaymentProcessor
    ├─ MSitefPaymentActivity    ← proxy de Activity (lança o M-SiTef)
    ├─ MSitefPaymentHolder      ← object singleton para callbacks
    ├─ MSitefResponse           ← mapeia Intent de retorno
    ├─ MSitefSettingsKey        ← constantes de chave
    ├─ Utils.kt                 ← helpers de data/hora/valor
    ├─ MSitefModule.kt          ← módulo Koin (injeção)
    └─ AndroidManifest.xml      ← declaração da Activity
```

---

## 3. Fluxo de Uma Transação

```
┌────────────────────────────────────────────────────────────┐
│ 1. Usuário clica "Pagar" no CheckoutActivity              │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 2. ViewModel chama:                                        │
│    paymentProcessor.processPayment(payment, callback)     │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 3. MSitefPaymentProcessor:                                 │
│    ├─ lê configurações de Settings (empresa, endereço…)   │
│    ├─ mapeia Payment → Intent extras para M-SiTef         │
│    ├─ salva callback em MSitefPaymentHolder               │
│    └─ abre MSitefPaymentActivity                          │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 4. MSitefPaymentActivity:                                  │
│    └─ startActivityForResult(fiservIntent, 1001)          │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 5. App M-SiTef processa a transação (usuario interage)    │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 6. M-SiTef retorna Intent com resultado:                  │
│    ├─ RESULT_OK → sucesso ou erro da transação           │
│    ├─ RESULT_CANCELED → usuário cancelou                  │
│    └─ extras → dados completos da transação              │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 7. MSitefPaymentActivity.onActivityResult():              │
│    ├─ lê MSitefResponse dos extras                        │
│    ├─ obtém callback de MSitefPaymentHolder               │
│    └─ dispara callback.onSuccess/onFailure/onCancelled   │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 8. Callback é executado no ViewModel:                     │
│    ├─ onSuccess   → atualizar UI com sucesso             │
│    ├─ onFailure   → mostrar erro e código                │
│    └─ onCancelled → desfazer alterações                  │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 9. MSitefPaymentHolder.clear() → limpa estado            │
└────────────────────────────────────────────────────────────┘
```

---

## 4. Classes e Responsabilidades

### 4.1 `MSitefPaymentProcessor`

**Visibilidade:** `internal` (encapsulada no módulo)  
**Injetado por:** Koin como `PaymentProcessor` (veja seção 7)

| Responsabilidade | Detalhes |
|---|---|
| Ler configurações | `Settings[EMPRESA_SITEF]`, `Settings[ENDERECO_SITEF]`, etc. |
| Mapear `PaymentType` | `DEBIT=2`, `CREDIT=3`, `PIX=122`, `VOUCHER=2` |
| Mapear restrições | Habilita modalidades no M-SiTef |
| Converter valor | `10.50 → "1050"` (sem ponto decimal) |
| Montar Intent SiTef | Action: `br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF` |
| Abrir Activity proxy | Inicia `MSitefPaymentActivity` |

**Exemplo interno:**
```kotlin
// MSitefPaymentProcessor.kt
internal class MSitefPaymentProcessor(
    private val context: Context
) : PaymentProcessor {
    override fun processPayment(payment: Payment, callback: PaymentCallback) {
        // 1. Salva callback no holder (será recuperado em onActivityResult)
        MSitefPaymentHolder.setCallback(callback)
        MSitefPaymentHolder.setPayment(payment)
        
        // 2. Lê configurações (vêm do Settings global)
        val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")
        val enderecoSitef = Settings.getValue(ENDERECO_SITEF, "")
        // ...
        
        // 3. Monta Intent para M-SiTef
        val fiservIntent = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
        fiservIntent.putExtra("empresaSitef", empresaSitef)
        fiservIntent.putExtra("endereco", enderecoSitef)
        // ... (mais extras)
        
        // 4. Abre Activity proxy
        val proxyIntent = Intent(context, MSitefPaymentActivity::class.java)
        proxyIntent.putExtra("fiserv_intent", fiservIntent)
        context.startActivity(proxyIntent)
    }
}
```

**Extras enviados ao M-SiTef:**

| Extra | Fonte | Exemplo |
|---|---|---|
| `empresaSitef` | `Settings[EMPRESA_SITEF]` | `"00000000"` |
| `enderecoSitef` | `Settings[ENDERECO_SITEF]` | `"192.168.1.1"` |
| `operador` | `Settings[OPERADOR]` | `"0001"` |
| `CNPJ_CPF` | `Settings[CNPJ_CPF]` | `"12345678912345"` |
| `cnpj_automacao` | `Settings[CNPJ_AUTOMACAO]` | `"12345678912345"` |
| `data` | `getCurrentDate()` | `"20260312"` (yyyyMMdd) |
| `hora` | `getCurrentTime()` | `"143022"` (HHmmss) |
| `numeroCupom` | data + hora | `"20260312143022"` |
| `valor` | `payment.amount.toStringWithoutDots()` | `"1050"` |
| `modalidade` | `mapPaymentMethod()` | `"3"` (CREDIT) |
| `numParcelas` | `payment.installmentDetails?.installments` | `"3"` (se parcelado) |
| `restricoes` | `mapRestriction()` | `"TransacoesHabilitadas=26"` |

---

### 4.2 `MSitefPaymentActivity`

Activity **transparente** que funciona como proxy entre o app e o M-SiTef.

**Funcionalidade:**
1. Recebe Intent com extras do M-SiTef encapsulados
2. Chama `startActivityForResult()` com código `1001`
3. Em `onActivityResult()`, interpreta resultado
4. Dispara callback (`onSuccess`, `onFailure`, `onCancelled`)
5. Limpa estado e fecha

**Declaração no AndroidManifest:**
```xml
<activity
    android:name="com.mjtech.fiserv.msitef.presentation.MSitefPaymentActivity"
    android:exported="false"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

---

### 4.3 `MSitefPaymentHolder`

`internal object` — **Singleton** que resolve o problema de comunicação entre classes.

**Problema:** Como passar um callback de `MSitefPaymentProcessor` para `MSitefPaymentActivity` via Intent?
- ❌ Não pode serializar função/lambda em Intent
- ❌ Não pode usar parâmetros de construtor (Activity é criada pelo SO)
- ✅ Usar singleton em memória para guardar referências

**Solução:**
```kotlin
internal object MSitefPaymentHolder {
    private var callback: PaymentCallback? = null
    private var payment: Payment? = null
    
    fun setCallback(cb: PaymentCallback) { callback = cb }
    fun setPayment(p: Payment) { payment = p }
    
    fun getCallback(): PaymentCallback? = callback
    fun getPayment(): Payment? = payment
    
    fun clear() {
        callback = null
        payment = null
    }
}
```

**Limitação importante:** ⚠️ Não inicie duas transações simultaneamente! O holder pode ser sobrescrito.

---

### 4.4 `MSitefResponse`

Data class que desserializa todos os extras retornados pelo M-SiTef.

```kotlin
data class MSitefResponse(
    val codResp: String?,           // "0" = sucesso
    val compDadosConf: String?,     // Dados de confirmação
    val codTrans: String?,          // Código da transação
    val redeAut: String?,           // Rede autorizadora
    val bandeira: String?,          // Bandeira do cartão
    val codAutorizacao: String?,    // Código de autorização
    // ... (mais campos)
    val viaEstabelecimento: String? // Comprovante para estabelecimento
)
```

---

### 4.5 `MSitefSettingsKey`

Constantes para ler/escrever Settings.

```kotlin
object MSitefSettingsKey {
    const val EMPRESA_SITEF = "EMPRESA_SITEF"
    const val ENDERECO_SITEF = "ENDERECO_SITEF"
    const val OPERADOR = "OPERADOR"
    const val CNPJ_CPF = "CNPJ_CPF"
    const val CNPJ_AUTOMACAO = "CNPJ_AUTOMACAO"
}
```

---

### 4.6 `Utils.kt`

Funções auxiliares para formatação.

| Função | Retorno | Exemplo |
|---|---|---|
| `getCurrentDate()` | `String` (yyyyMMdd) | `"20260312"` |
| `getCurrentTime()` | `String` (HHmmss) | `"143022"` |
| `Double.toStringWithoutDots()` | `String` sem `.` | `10.50 → "1050"` |
| `String.getFullAddress()` | `String` endereço completo | `"192.168.1.1;192.168.1.1:20036"` |

---

## 5. Dependências do Módulo

### 5.1 Módulos internos

```
:domain
```

### 5.2 Dependências externas (Gradle)

No `build.gradle.kts` do módulo `:fiserv:msitef`:

```kotlin
dependencies {
    implementation(project(":domain"))
    
    // Koin (Injeção de Dependência)
    implementation("io.insert-koin:koin-android:3.5.0")
    
    // Android
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
}
```

### 5.3 Dependência de runtime

O módulo **NÃO inclui** o app M-SiTef. Ele precisa estar **instalado no device**. O pacote resolvido é:

```
Ação de Intent: br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF
Pacote: br.com.softwareexpress.sitef.msitef (M-SiTef Oficial)
```

---

## 6. Configuração Inicial (Settings)

Antes de processar qualquer pagamento, popule o objeto `Settings` com as configurações do SiTef.

### Quando populrar?

Idealmente em **`Application.onCreate()`** após carregar do banco/DataStore:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 1. Iniciar Koin com módulos
        startKoin {
            androidContext(this@MyApplication)
            modules(msitefModule)
        }
        
        // 2. Carregar do DataStore/SharedPreferences
        // (isso é assíncrono, pode usar viewModelScope em ViewModel de splash)
        
        // 3. Popular Settings (sincronamente)
        Settings.updateSetting(Setting(EMPRESA_SITEF,    "00000000"))
        Settings.updateSetting(Setting(ENDERECO_SITEF,   "192.168.1.100"))
        Settings.updateSetting(Setting(OPERADOR,         "0001"))
        Settings.updateSetting(Setting(CNPJ_CPF,         "12345678000195"))
        Settings.updateSetting(Setting(CNPJ_AUTOMACAO,   "12345678000195"))
    }
}
```

### Exemplo com DataStore

```kotlin
// No SplashViewModel
suspend fun initializeSettings(dataStore: DataStore<Preferences>) {
    dataStore.data.first().let { prefs ->
        Settings.updateSetting(Setting(
            EMPRESA_SITEF, 
            prefs[stringPreferencesKey(EMPRESA_SITEF)] ?: "00000000"
        ))
        Settings.updateSetting(Setting(
            ENDERECO_SITEF,
            prefs[stringPreferencesKey(ENDERECO_SITEF)] ?: "192.168.1.100"
        ))
        // ... repetir para outros
    }
}
```

---

## 7. Como Usar no Seu App (com Koin) — Padrão Recomendado

> ⚠️ **Importante:** Este é o padrão **recomendado** para novos projetos. Se você está começando a integrar do zero, use Koin. Veja seção 10 para usar Hilt.

### 7.1 Adicionar dependências no `build.gradle.kts` do app

```kotlin
dependencies {
    implementation(project(":domain"))
    implementation(project(":fiserv:msitef"))
    
    // Koin para DI
    implementation("io.insert-koin:koin-android:3.5.0")
}
```

### 7.2 Criar `Application.kt`

```kotlin
import android.app.Application
import com.mjtech.fiserv.msitef.di.msitefModule
import com.mjtech.domain.settings.model.Setting
import com.mjtech.domain.settings.model.Settings
import com.mjtech.fiserv.msitef.common.MSitefSettingsKey.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // ✅ 1. Iniciar Koin com módulo do M-SiTef
        startKoin {
            androidContext(this@MyApplication)
            modules(
                msitefModule  // importado de com.mjtech.fiserv.msitef.di
            )
        }
        
        // ✅ 2. Popular configurações do SiTef
        Settings.updateSetting(Setting(EMPRESA_SITEF,    "00000000"))
        Settings.updateSetting(Setting(ENDERECO_SITEF,   "192.168.1.100"))
        Settings.updateSetting(Setting(OPERADOR,         "0001"))
        Settings.updateSetting(Setting(CNPJ_CPF,         "12345678000195"))
        Settings.updateSetting(Setting(CNPJ_AUTOMACAO,   "12345678000195"))
    }
}
```

### 7.3 Declarar `Application` no `AndroidManifest.xml`

```xml
<application
    android:name=".MyApplication"
    ...>
```

### 7.4 Criar ViewModel com injeção

```kotlin
import androidx.lifecycle.ViewModel
import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.payment.repository.PaymentCallback
import com.mjtech.domain.payment.model.Payment
import com.mjtech.domain.payment.model.PaymentType
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor  // injetado pelo Koin!
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
                // ✅ Sucesso! Atualizar UI
                println("Pagamento aprovado: $transactionId")
            }
            
            override fun onFailure(errorCode: String, errorMessage: String) {
                // ❌ Erro na transação
                println("Erro $errorCode: $errorMessage")
            }
            
            override fun onCancelled(message: String?) {
                // ⚠️ Usuário cancelou
                println("Transação cancelada")
            }
        })
    }
}
```

### 7.5 Usar ViewModel com Koin na Activity

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : ComponentActivity() {
    
    private val viewModel: CheckoutViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // Sua UI aqui
            Button(onClick = { viewModel.pay(100.00) }) {
                Text("Pagar R$ 100,00")
            }
        }
    }
}
```

### 7.6 Estrutura final do app

```
app/
├── MyApplication.kt         ← inicializa Koin
├── AndroidManifest.xml      ← declara Application
├── ui/
│   ├── checkout/
│   │   ├── CheckoutActivity.kt
│   │   ├── CheckoutViewModel.kt  ← usa PaymentProcessor
│   │   └── CheckoutComposable.kt
│   └── settings/
│       ├── SettingsActivity.kt
│       └── SettingsViewModel.kt
└── data/
    └── repository/
        └── SettingsRepositoryImpl.kt  ← persiste em DataStore
```

---

## 8. Modelos do Domínio

### `Payment`

```kotlin
data class Payment(
    val id: Long,                           // ID único da transação
    val amount: Double,                     // valor em reais (ex: 10.50)
    val type: PaymentType,                  // DEBIT, CREDIT, PIX, VOUCHER...
    val installmentDetails: InstallmentDetails?  // null = à vista
)
```

### `PaymentType`

```kotlin
enum class PaymentType(val text: String) {
    DEBIT("Débito"),
    CREDIT("Crédito"),
    PIX("Pix"),
    VOUCHER("Voucher"),
    INSTANT_PAYMENT("Pagamento Instantâneo")
}
```

### `InstallmentDetails`

```kotlin
data class InstallmentDetails(
    val installments: Int,                  // número de parcelas (ex: 3)
    val installmentType: InstallmentType    // NONE, MERCHANT, ISSUER
)
```

### `InstallmentType`

```kotlin
enum class InstallmentType(val text: String) {
    NONE("Sem parcelamento"),
    MERCHANT("Parcelamento da loja"),
    ISSUER("Parcelamento do banco")
}
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

## 9. Códigos de Erro Conhecidos

| Código | Origem | Descrição | Ação Recomendada |
|---|---|---|---|
| `"0"` | M-SiTef | **✅ Sucesso** | Processar confirmação |
| Qualquer outro `codResp` | M-SiTef | Erro específico do SiTef | Mostrar mensagem ao usuário |
| `INTEGRATION_ERROR` | Módulo | App M-SiTef não instalado | Avisar para instalar SiTef |
| `INVALID_DATA` | Módulo | Payment ou callback nulo | Revisar dados antes de chamar |
| `FISERV_ERROR` | Módulo | Result code inesperado | Log e contactar suporte |
| `UNKNOWN_ERROR` | Módulo | Resposta sem codResp | Log e contactar suporte |

---

## 10. Integração em Apps com Hilt

Se seu app já usa **Hilt** para injeção de dependência, veja este documento separado:  
📖 **[msitef-hilt-migration.md](./msitef-hilt-migration.md)**

Ele oferece **duas opções**:
1. **Opção A:** Koin + Hilt coexistindo (rápido, ~15 min)
2. **Opção B:** Migrar o módulo para Hilt nativo (completo, ~1-2 h)

---

## 11. Perguntas Frequentes

### P: Por que usar Singleton (`Settings`) em vez de injetar as configurações?

**R:** O Settings é um padrão especial para configurações globais:
- ✅ Acesso instantâneo em qualquer lugar (não precisa injeção)
- ✅ Atualização em tempo real (quando usuário edita configurações)
- ✅ Performance (RAM vs disco)

O "custo" é que é um estado global. Mas para configurações de sistema (não de lógica de negócio), é apropriado.

---

### P: Posso processar dois pagamentos simultaneamente?

**R:** ❌ **Não.** O `MSitefPaymentHolder` é um singleton que armazena apenas um callback/payment.

Se você chamar `processPayment()` duas vezes antes de completar a primeira, a segunda chamada sobrescreverá o holder.

**Solução:** Garanta que apenas um pagamento está em andamento. Use uma flag no ViewModel:

```kotlin
private var isProcessing = false

fun pay(amount: Double) {
    if (isProcessing) {
        Toast.makeText(context, "Aguarde a transação anterior", Toast.LENGTH_SHORT).show()
        return
    }
    isProcessing = true
    // ... chamar processPayment
    // Definir isProcessing = false quando receber callback
}
```

---

### P: Como saber se o M-SiTef está instalado?

**R:** Tente resolver a Intent:

```kotlin
val intent = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
val canResolve = context.packageManager.resolveActivity(intent, 0) != null

if (!canResolve) {
    Toast.makeText(context, "M-SiTef não instalado", Toast.LENGTH_SHORT).show()
    // Levar usuário para Play Store
}
```

---

### P: Como persistir as configurações do SiTef?

**R:** Use DataStore ou SharedPreferences:

```kotlin
// ViewModel de Configurações
class SettingsViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    
    fun saveEmpresaSitef(value: String) = viewModelScope.launch {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("EMPRESA_SITEF")] = value
        }
        // Também atualizar o singleton
        Settings.updateSetting(Setting(EMPRESA_SITEF, value))
    }
}
```

---

### P: Posso usar este módulo sem Koin?

**R:** ✅ Sim, mas precisaria criar a instância manualmente:

```kotlin
// Não recomendado, mas possível:
val context = applicationContext
val paymentProcessor = MSitefPaymentProcessor(context)
paymentProcessor.processPayment(payment, callback)
```

Mas **Koin é recomendado** porque:
- Centraliza a criação de dependências
- Facilita testes (substituir implementações)
- É padrão moderno no Android

---

### P: Qual é a diferença entre `PaymentProcessor` e `PaymentCallback`?

**R:** 
- **`PaymentProcessor`** → **Você chama** para iniciar um pagamento
- **`PaymentCallback`** → **O módulo chama** quando o pagamento termina

```kotlin
// Você implementa o callback:
object : PaymentCallback {
    override fun onSuccess(...) { /* seu código */ }
    override fun onFailure(...) { /* seu código */ }
    override fun onCancelled(...) { /* seu código */ }
}

// O módulo injeta o processor:
private val paymentProcessor: PaymentProcessor  // Koin injeta
```

---

