# Módulo Domain — Documentação Completa

> **Versão:** 1.0 · **Data:** 2026-02-27  
> **Namespace:** `com.mjtech.domain`

---

## 📋 Índice

1. [O que é o Módulo Domain?](#1-o-que-é-o-módulo-domain)
2. [Conceitos Fundamentais](#2-conceitos-fundamentais)
3. [Estrutura do Módulo](#3-estrutura-do-módulo)
4. [Settings — Sistema de Configuração](#4-settings--sistema-de-configuração)
5. [Payment — Sistema de Pagamento](#5-payment--sistema-de-pagamento)
6. [Fluxo Completo de Uma Transação](#6-fluxo-completo-de-uma-transação)
7. [Vantagens desta Arquitetura](#7-vantagens-desta-arquitetura)
8. [Exemplo Prático de Uso](#8-exemplo-prático-de-uso)

---

## 1. O que é o Módulo Domain?

O módulo `:domain` é o **coração da arquitetura** do aplicativo. Ele contém:

- ✅ **Modelos de dados puros** (sem dependência do Android)
- ✅ **Interfaces de contratos** que definem como as coisas devem funcionar
- ✅ **Lógica de negócio compartilhada** entre todos os módulos
- ✅ **Nenhuma implementação específica** de plataforma (Android, banco de dados, API, etc.)

### Por que separar o Domain?

```
┌─────────────────────────────────────────────────────┐
│  APP MODULE                                         │
│  ├─ UI (Activities, ViewModels, Composables)       │
│  └─ depende de → DOMAIN                             │
└─────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────┐
│  DOMAIN MODULE (Pure Kotlin)                        │
│  ├─ Modelos (data class, enum)                     │
│  ├─ Interfaces (PaymentProcessor, Repository)      │
│  └─ Lógica comum (Settings singleton)              │
└─────────────────────────────────────────────────────┘
              ↑
┌─────────────────────────────────────────────────────┐
│  MSITEF MODULE                                      │
│  └─ Implementa → PaymentProcessor                  │
└─────────────────────────────────────────────────────┘
```

**Vantagem:** Se você quiser trocar de integração (ex: de M-SiTef para PagSeguro), você só precisa criar um novo módulo que implemente as mesmas interfaces. O app não precisa mudar nada!

---

## 2. Conceitos Fundamentais

### 2.1 O que é uma `data class`?

Uma `data class` em Kotlin é uma classe especial que existe **apenas para carregar dados**.

#### Exemplo básico:
```kotlin
data class Pessoa(
    val nome: String,
    val idade: Int
)
```

#### O que ela ganha automaticamente:
- ✅ `toString()` → `Pessoa(nome=João, idade=30)`
- ✅ `equals()` e `hashCode()` → compara valores, não referências
- ✅ `copy()` → cria cópias com modificações
- ✅ Desestruturação → `val (nome, idade) = pessoa`

#### No nosso caso:
```kotlin
data class Payment(
    val id: Long,
    val amount: Double,
    val type: PaymentType,
    val installmentDetails: InstallmentDetails?
)
```

É uma forma **limpa e imutável** de representar um pagamento. Comparação com Java:

```java
// Em Java você precisaria de:
public class Payment {
    private final long id;
    private final double amount;
    // ... + getters + equals + hashCode + toString (50+ linhas)
}

// Em Kotlin:
data class Payment(val id: Long, val amount: Double) // 1 linha!
```

---

### 2.2 O que é uma `interface`?

Uma **interface** é um **contrato** que define o QUE deve ser feito, mas não o COMO.

#### Exemplo no projeto:
```kotlin
interface PaymentProcessor {
    fun processPayment(payment: Payment, callback: PaymentCallback)
}
```

**Tradução:** "Qualquer classe que implemente `PaymentProcessor` deve ter um método `processPayment`."

#### Quem implementa?
```kotlin
// No módulo :fiserv:msitef
internal class MSitefPaymentProcessor(
    private val context: Context
) : PaymentProcessor {
    
    override fun processPayment(payment: Payment, callback: PaymentCallback) {
        // Implementação específica do M-SiTef
        val intent = Intent("br.com.softwareexpress.sitef.msitef...")
        // ...
    }
}
```

#### Por que usar interfaces?

**Sem interface (ruim):**
```kotlin
class CheckoutViewModel(
    private val msitefProcessor: MSitefPaymentProcessor // acoplado!
) {
    fun pay() {
        msitefProcessor.processPayment(...)
    }
}
```

❌ Se você quiser usar outro processador, precisa reescrever o ViewModel.

**Com interface (bom):**
```kotlin
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor // qualquer implementação!
) {
    fun pay() {
        paymentProcessor.processPayment(...) // não importa qual implementação
    }
}
```

✅ Você pode trocar a implementação sem tocar no ViewModel!

---

### 2.3 O que é um `object` (Singleton)?

Em Kotlin, `object` é uma forma de criar um **Singleton** (instância única) de forma automática.

#### Singleton tradicional em Java:
```java
public class Settings {
    private static Settings INSTANCE;
    
    private Settings() {} // construtor privado
    
    public static Settings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Settings();
        }
        return INSTANCE;
    }
}

// Uso:
Settings.getInstance().updateSetting(...);
```

#### Singleton em Kotlin:
```kotlin
object Settings {
    fun updateSetting(...) {
        // ...
    }
}

// Uso:
Settings.updateSetting(...) // pronto! Kotlin cuida do resto
```

**O que acontece internamente:**
- Kotlin cria automaticamente uma instância única
- É **thread-safe** (seguro para múltiplas threads)
- A instância é criada quando primeira vez acessada (**lazy initialization**)

---

### 2.4 O que é um `enum class`?

Um `enum` representa um conjunto **fixo e limitado** de valores possíveis.

```kotlin
enum class PaymentType(val text: String) {
    DEBIT("Débito"),
    CREDIT("Crédito"),
    PIX("Pix"),
    VOUCHER("Voucher"),
    INSTANT_PAYMENT("Pagamento Instantâneo")
}
```

**Uso:**
```kotlin
val tipo = PaymentType.CREDIT
println(tipo.text) // "Crédito"

when (tipo) {
    PaymentType.DEBIT -> println("É débito")
    PaymentType.CREDIT -> println("É crédito")
    PaymentType.PIX -> println("É Pix")
    PaymentType.VOUCHER -> println("É voucher")
    PaymentType.INSTANT_PAYMENT -> println("É pagamento instantâneo")
}
```

**Vantagem:** O compilador garante que você só use valores válidos. Não pode fazer:
```kotlin
val tipo = "debito" // erro de compilação!
```

---

### 2.5 O que é uma `sealed class`?

Uma `sealed class` é como um `enum` **turbinado**. Permite criar uma hierarquia fechada de tipos.

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Uso:**
```kotlin
fun handleResult(result: Result<Payment>) {
    when (result) {
        is Result.Success -> println("Pagamento: ${result.data}")
        is Result.Error -> println("Erro: ${result.error}")
        is Result.Loading -> println("Carregando...")
    }
    // O compilador garante que todos os casos estão cobertos!
}
```

**Diferença de enum:**
- `enum` → todos os valores são instâncias da mesma classe
- `sealed class` → pode ter subclasses diferentes com dados diferentes

---

## 3. Estrutura do Módulo

```
domain/
├── common/
│   └── Result.kt                          ← sealed class para resultados
│
├── settings/
│   ├── model/
│   │   ├── Setting.kt                     ← data class para uma configuração
│   │   └── Settings.kt                    ← object singleton (gerenciador global)
│   └── repository/
│       └── SettingsRepository.kt          ← interface para persistência
│
├── payment/
│   ├── model/
│   │   ├── Payment.kt                     ← data class principal
│   │   ├── PaymentType.kt                 ← enum de tipos
│   │   ├── InstallmentDetails.kt          ← data class para parcelamento
│   │   ├── InstallmentType.kt             ← enum de tipos de parcelamento
│   │   ├── PaymentMethod.kt               ← data class para métodos disponíveis
│   │   └── InstallmentOption.kt           ← data class para opções de parcelas
│   └── repository/
│       ├── PaymentProcessor.kt            ← interface para processar pagamento
│       ├── PaymentCallback.kt             ← interface de callback
│       └── PaymentRepository.kt           ← interface para gerenciar métodos
│
└── print/
    ├── model/
    │   ├── TextPrint.kt                   ← data class para impressão
    │   ├── TextStyle.kt                   ← data class para estilo
    │   └── ImageData.kt                   ← data class para imagem
    └── repository/
        └── PrintRepository.kt             ← interface para impressão
```

---

## 4. Settings — Sistema de Configuração

### 4.1 Arquitetura

```kotlin
// 1. Modelo de uma configuração
data class Setting(
    val key: String,
    val value: Any
)

// 2. Gerenciador global (Singleton)
object Settings {
    private val _settingsMap = mutableMapOf<String, Any>()
    
    fun updateSetting(setting: Setting) {
        _settingsMap[setting.key] = setting.value
    }
    
    inline fun <reified T> getValue(key: String, defaultValue: T): T {
        return _settingsMap[key] as? T ?: defaultValue
    }
}

// 3. Interface para persistência (implementada no módulo app)
interface SettingsRepository {
    fun getSetting(setting: Setting): Flow<Result<Setting>>
    fun saveSetting(setting: Setting): Flow<Result<Unit>>
}
```

### 4.2 Fluxo de Configuração

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. Usuário abre tela de configurações                          │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│ 2. SettingsViewModel carrega Settings.settingsMap              │
│    (valores já carregados do banco/datastore no app startup)   │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│ 3. Usuário edita um campo (ex: "EMPRESA_SITEF" = "00000000")  │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. ViewModel chama:                                            │
│    settingsRepository.saveSetting(Setting("EMPRESA_SITEF", ...))│
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. Repository salva no banco/DataStore (persistência)         │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6. ViewModel atualiza o singleton:                            │
│    Settings.updateSetting(Setting("EMPRESA_SITEF", ...))       │
└─────────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│ 7. Agora qualquer lugar do app pode acessar:                  │
│    Settings.getValue("EMPRESA_SITEF", "")                      │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 Por que usar um Singleton para Settings?

**Vantagens:**
1. ✅ **Acesso global** → qualquer classe pode ler configurações
2. ✅ **Memória compartilhada** → não precisa passar por parâmetros
3. ✅ **Performance** → leitura instantânea (está na RAM)
4. ✅ **Simples** → não precisa injeção de dependência para ler

**Exemplo prático:**
```kotlin
// No MSitefPaymentProcessor
val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")
val enderecoSitef = Settings.getValue(ENDERECO_SITEF, "")

// Não precisa passar esses valores por parâmetros!
```

**Quando o Settings é carregado?**
```kotlin
// No Application.onCreate() ou ViewModel inicial
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Carrega configurações salvas do banco/DataStore
        loadSettingsFromPersistence { listOfSettings ->
            listOfSettings.forEach { setting ->
                Settings.updateSetting(setting)
            }
        }
    }
}
```

---

## 5. Payment — Sistema de Pagamento

### 5.1 Modelos de Dados

#### `Payment` (data class)
```kotlin
data class Payment(
    val id: Long,                          // ID único da transação
    val amount: Double,                    // Valor em reais (ex: 10.50)
    val type: PaymentType,                 // DEBIT, CREDIT, PIX, etc.
    val installmentDetails: InstallmentDetails?  // null se não parcelado
)
```

#### `PaymentType` (enum)
```kotlin
enum class PaymentType(val text: String) {
    DEBIT("Débito"),
    CREDIT("Crédito"),
    PIX("Pix"),
    VOUCHER("Voucher"),
    INSTANT_PAYMENT("Pagamento Instantâneo")
}
```

#### `InstallmentDetails` (data class)
```kotlin
data class InstallmentDetails(
    val installments: Int,                 // Número de parcelas (2, 3, 6, 12...)
    val installmentType: InstallmentType   // NONE, MERCHANT, ISSUER
)
```

#### `InstallmentType` (enum)
```kotlin
enum class InstallmentType(val text: String) {
    NONE("NONE"),         // À vista
    MERCHANT("MERCHANT"), // Parcelado sem juros (lojista)
    ISSUER("ISSUER")      // Parcelado com juros (emissor)
}
```

### 5.2 Interfaces de Contrato

#### `PaymentProcessor` (interface)
```kotlin
interface PaymentProcessor {
    fun processPayment(payment: Payment, callback: PaymentCallback)
}
```

**Responsabilidade:** Processar uma transação de pagamento.

**Implementadores:**
- `MSitefPaymentProcessor` (no módulo `:fiserv:msitef`)
- `PagSeguroPaymentProcessor` (hipotético)
- `MockPaymentProcessor` (para testes)

#### `PaymentCallback` (interface)
```kotlin
interface PaymentCallback {
    fun onSuccess(transactionId: String, message: String? = null)
    fun onFailure(errorCode: String, errorMessage: String)
    fun onCancelled(message: String? = null)
}
```

**Responsabilidade:** Notificar o resultado da transação.

**Por que usar callback em vez de Flow/suspend?**

O `PaymentProcessor` precisa abrir uma Activity externa (M-SiTef) e esperar o resultado via `onActivityResult`. Isso não é uma operação suspend natural, então callback é mais apropriado.

### 5.3 Exemplo de Implementação

```kotlin
// No módulo :fiserv:msitef
internal class MSitefPaymentProcessor(
    private val context: Context
) : PaymentProcessor {

    override fun processPayment(payment: Payment, callback: PaymentCallback) {
        // 1. Lê configurações do Settings
        val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")
        
        // 2. Monta Intent para o M-SiTef
        val intent = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
        intent.putExtra("empresaSitef", empresaSitef)
        intent.putExtra("valor", payment.amount.toStringWithoutDots())
        // ...
        
        // 3. Salva callback para uso posterior
        MSitefPaymentHolder.initialize(callback, payment)
        
        // 4. Abre Activity proxy
        val proxyIntent = Intent(context, MSitefPaymentActivity::class.java)
        proxyIntent.putExtra("fiserv_intent", intent)
        context.startActivity(proxyIntent)
    }
}
```

---

## 6. Fluxo Completo de Uma Transação

### 6.1 Diagrama de Sequência

```
┌──────────┐  ┌──────────────┐  ┌────────────────────┐  ┌─────────────────────┐  ┌──────────┐
│ UI/User  │  │ ViewModel    │  │ PaymentProcessor   │  │ MSitefPaymentActivity│  │ M-SiTef  │
└────┬─────┘  └──────┬───────┘  └─────────┬──────────┘  └──────────┬──────────┘  └────┬─────┘
     │               │                     │                        │                   │
     │ 1. Clica em   │                     │                        │                   │
     │   "Pagar"     │                     │                        │                   │
     │──────────────>│                     │                        │                   │
     │               │                     │                        │                   │
     │               │ 2. Cria Payment     │                        │                   │
     │               │    e callback       │                        │                   │
     │               │                     │                        │                   │
     │               │ 3. processPayment() │                        │                   │
     │               │────────────────────>│                        │                   │
     │               │                     │                        │                   │
     │               │                     │ 4. Lê Settings         │                   │
     │               │                     │    (empresa, endereço) │                   │
     │               │                     │                        │                   │
     │               │                     │ 5. Salva callback em   │                   │
     │               │                     │    MSitefPaymentHolder │                   │
     │               │                     │                        │                   │
     │               │                     │ 6. startActivity()     │                   │
     │               │                     │───────────────────────>│                   │
     │               │                     │                        │                   │
     │               │                     │                        │ 7. Lança M-SiTef  │
     │               │                     │                        │──────────────────>│
     │               │                     │                        │                   │
     │               │                     │                        │                   │
     │               │                     │                        │ 8. Usuário passa  │
     │               │                     │                        │    o cartão       │
     │               │                     │                        │<──────────────────│
     │               │                     │                        │                   │
     │               │                     │                        │ 9. onActivityResult│
     │               │                     │                        │<──────────────────│
     │               │                     │                        │    (RESULT_OK)    │
     │               │                     │                        │                   │
     │               │                     │                        │ 10. Parseia resposta│
     │               │                     │                        │     MSitefResponse │
     │               │                     │                        │                   │
     │               │                     │                        │ 11. Recupera callback│
     │               │                     │                        │     do Holder      │
     │               │                     │                        │                   │
     │               │ 12. callback.onSuccess()                     │                   │
     │               │<─────────────────────────────────────────────│                   │
     │               │    (transactionId, comprovante)              │                   │
     │               │                     │                        │                   │
     │ 13. Atualiza UI│                    │                        │ 14. finish()      │
     │    (sucesso)  │                     │                        │                   │
     │<──────────────│                     │                        │                   │
     │               │                     │                        │                   │
```

### 6.2 Código Passo a Passo

**1. ViewModel cria o Payment:**
```kotlin
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor
) : ViewModel() {

    fun pay(amount: Double) {
        val payment = Payment(
            id = System.currentTimeMillis(),
            amount = amount,
            type = PaymentType.CREDIT,
            installmentDetails = InstallmentDetails(
                installments = 3,
                installmentType = InstallmentType.MERCHANT
            )
        )
        
        paymentProcessor.processPayment(payment, paymentCallback)
    }
    
    private val paymentCallback = object : PaymentCallback {
        override fun onSuccess(transactionId: String, message: String?) {
            // Atualizar UI com sucesso
            _uiState.update { it.copy(result = "Pagamento aprovado!") }
        }
        
        override fun onFailure(errorCode: String, errorMessage: String) {
            // Atualizar UI com erro
            _uiState.update { it.copy(result = "Erro: $errorMessage") }
        }
        
        override fun onCancelled(message: String?) {
            // Atualizar UI com cancelamento
            _uiState.update { it.copy(result = "Transação cancelada") }
        }
    }
}
```

**2. MSitefPaymentProcessor processa:**
```kotlin
internal class MSitefPaymentProcessor(private val context: Context) : PaymentProcessor {

    override fun processPayment(payment: Payment, callback: PaymentCallback) {
        try {
            // Lê configurações
            val empresaSitef = Settings.getValue(EMPRESA_SITEF, "")
            val enderecoSitef = Settings.getValue(ENDERECO_SITEF, "")
            
            // Monta Intent
            val intent = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
            intent.putExtra("empresaSitef", empresaSitef)
            intent.putExtra("enderecoSitef", enderecoSitef.getFullAddress())
            intent.putExtra("valor", payment.amount.toStringWithoutDots())
            intent.putExtra("modalidade", mapPaymentMethod(payment.type))
            // ...
            
            // Salva callback para recuperar depois
            MSitefPaymentHolder.initialize(callback, payment)
            
            // Abre Activity proxy
            val proxyIntent = Intent(context, MSitefPaymentActivity::class.java)
            proxyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            proxyIntent.putExtra("fiserv_intent", intent)
            context.startActivity(proxyIntent)
            
        } catch (e: Exception) {
            callback.onFailure("INTEGRATION_ERROR", e.message ?: "Erro desconhecido")
        }
    }
}
```

**3. MSitefPaymentActivity recebe o resultado:**
```kotlin
class MSitefPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val fiservIntent = intent.getParcelableExtra<Intent>("fiserv_intent")
        if (fiservIntent == null) {
            MSitefPaymentHolder.callback?.onFailure("INVALID_DATA", "Intent nula")
            finish()
            return
        }
        
        startActivityForResult(fiservIntent, REQUEST_CODE_MSITEF)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode != REQUEST_CODE_MSITEF) return
        
        val callback = MSitefPaymentHolder.callback
        
        when (resultCode) {
            RESULT_OK -> {
                val response = MSitefResponse.fromIntent(data)
                if (response.codResp == "0") {
                    callback?.onSuccess(
                        response.nsuSitef ?: "UNKNOWN",
                        response.viaEstabelecimento
                    )
                } else {
                    callback?.onFailure(
                        response.codResp ?: "UNKNOWN_ERROR",
                        "Erro na transação"
                    )
                }
            }
            RESULT_CANCELED -> {
                callback?.onCancelled("Transação cancelada pelo usuário")
            }
            else -> {
                callback?.onFailure("FISERV_ERROR", "Erro inesperado")
            }
        }
        
        MSitefPaymentHolder.clear()
        finish()
    }
}
```

**4. MSitefPaymentHolder (Singleton de comunicação):**
```kotlin
internal object MSitefPaymentHolder {
    var callback: PaymentCallback? = null
    var payment: Payment? = null

    fun initialize(callback: PaymentCallback, payment: Payment) {
        this.callback = callback
        this.payment = payment
    }

    fun clear() {
        this.callback = null
        this.payment = null
    }
}
```

**Por que usar outro Singleton aqui?**

O `MSitefPaymentHolder` é diferente do `Settings`. Ele é um **holder temporário** usado apenas durante uma transação:

1. `MSitefPaymentProcessor` salva o callback
2. `MSitefPaymentActivity` recupera o callback
3. Após executar o callback, limpa tudo

**Não é para armazenar configurações permanentes**, é apenas para "passar" o callback entre Activity e Processor (já que não dá pra passar callback via Intent).

---

## 7. Vantagens desta Arquitetura

### 7.1 Separação de Responsabilidades

```
┌─────────────────────────────────────────────────────────────────┐
│ DOMAIN (o QUE fazer)                                           │
│ - Define interfaces                                            │
│ - Define modelos                                               │
│ - Não sabe como será implementado                             │
└─────────────────────────────────────────────────────────────────┘
                          ↑
                          │ implementa
                          │
┌─────────────────────────────────────────────────────────────────┐
│ IMPLEMENTAÇÃO (COMO fazer)                                     │
│ - MSitef module                                                │
│ - PagSeguro module (hipotético)                                │
│ - Mock module (para testes)                                    │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 Testabilidade

**Fácil criar mocks:**
```kotlin
class MockPaymentProcessor : PaymentProcessor {
    override fun processPayment(payment: Payment, callback: PaymentCallback) {
        // Simula sucesso instantâneo
        callback.onSuccess("MOCK_123", "Comprovante mock")
    }
}

// No teste:
val viewModel = CheckoutViewModel(MockPaymentProcessor())
viewModel.pay(100.0)
// Verifica se UI atualizou corretamente
```

### 7.3 Flexibilidade

**Trocar implementação sem quebrar nada:**
```kotlin
// Koin module
val appModule = module {
    // Fase 1: usa M-SiTef
    single<PaymentProcessor> { MSitefPaymentProcessor(get()) }
    
    // Fase 2: quer testar PagSeguro? Só troca aqui!
    // single<PaymentProcessor> { PagSeguroPaymentProcessor(get()) }
}
```

O `CheckoutViewModel` não muda nada, continua usando `PaymentProcessor`.

### 7.4 Reutilização

Os modelos do domain podem ser usados em:
- Aplicativo Android
- Aplicativo iOS (com KMP - Kotlin Multiplatform)
- Backend (se usar Kotlin no servidor)
- Testes unitários puros (sem Android)

---

## 8. Exemplo Prático de Uso

### 8.1 Setup Inicial (Application.kt)

```kotlin
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 1. Iniciar Koin
        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule,        // define repositories, viewModels
                msitefModule      // define PaymentProcessor
            )
        }
        
        // 2. Carregar configurações salvas
        loadSettingsFromDataStore()
    }
    
    private fun loadSettingsFromDataStore() {
        lifecycleScope.launch {
            val savedSettings = settingsRepository.getAllSettings()
            savedSettings.forEach { setting ->
                Settings.updateSetting(setting)
            }
        }
    }
}
```

### 8.2 Salvando Configurações (SettingsScreen)

```kotlin
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        OutlinedTextField(
            value = uiState.editableSettings[EMPRESA_SITEF] as? String ?: "",
            onValueChange = { newValue ->
                viewModel.updateEmpresaSitef(newValue)
            },
            label = { Text("Empresa SiTef") }
        )
        
        // Outros campos...
    }
}

// ViewModel
class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    fun updateEmpresaSitef(newValue: String) {
        val setting = Setting(EMPRESA_SITEF, newValue)
        
        viewModelScope.launch {
            // 1. Salva no banco/DataStore
            settingsRepository.saveSetting(setting).collect { result ->
                if (result is Result.Success) {
                    // 2. Atualiza o singleton
                    Settings.updateSetting(setting)
                }
            }
        }
    }
}
```

### 8.3 Processando Pagamento (CheckoutScreen)

```kotlin
@Composable
fun CheckoutScreen(viewModel: CheckoutViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        Text("Valor: R$ ${uiState.transactionAmount}")
        
        Button(onClick = { viewModel.confirmPayment() }) {
            Text("Confirmar Pagamento")
        }
        
        uiState.transactionResult?.let { result ->
            if (result.isSuccess) {
                Text("✅ ${result.message}", color = Color.Green)
            } else {
                Text("❌ ${result.message}", color = Color.Red)
            }
        }
    }
}

// ViewModel
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()
    
    fun confirmPayment() {
        val payment = Payment(
            id = System.currentTimeMillis(),
            amount = _uiState.value.transactionAmount,
            type = PaymentType.CREDIT,
            installmentDetails = null
        )
        
        paymentProcessor.processPayment(payment, object : PaymentCallback {
            override fun onSuccess(transactionId: String, message: String?) {
                _uiState.update {
                    it.copy(
                        transactionResult = TransactionResult(
                            isSuccess = true,
                            message = "Pagamento aprovado! NSU: $transactionId"
                        )
                    )
                }
            }
            
            override fun onFailure(errorCode: String, errorMessage: String) {
                _uiState.update {
                    it.copy(
                        transactionResult = TransactionResult(
                            isSuccess = false,
                            message = "Erro $errorCode: $errorMessage"
                        )
                    )
                }
            }
            
            override fun onCancelled(message: String?) {
                _uiState.update {
                    it.copy(
                        transactionResult = TransactionResult(
                            isSuccess = false,
                            message = message ?: "Transação cancelada"
                        )
                    )
                }
            }
        })
    }
}
```

---

## 9. Comparação com Outras Abordagens

### 9.1 Abordagem 1: Tudo no ViewModel (❌ Ruim)

```kotlin
class CheckoutViewModel(private val context: Context) : ViewModel() {
    
    fun pay() {
        // ViewModel acoplado ao M-SiTef
        val intent = Intent("br.com.softwareexpress.sitef.msitef...")
        intent.putExtra("empresaSitef", "00000000")
        context.startActivity(intent)
    }
}
```

**Problemas:**
- ❌ Não pode trocar de processador
- ❌ Difícil de testar (precisa do contexto real)
- ❌ ViewModel conhece detalhes de implementação

### 9.2 Abordagem 2: Repository sem Interface (❌ Ruim)

```kotlin
class CheckoutViewModel(
    private val msitefRepository: MSitefRepository // classe concreta
) : ViewModel() {
    fun pay() {
        msitefRepository.processPayment(...)
    }
}
```

**Problemas:**
- ❌ Acoplado a uma implementação específica
- ❌ Não pode trocar facilmente

### 9.3 Abordagem 3: Domain + Interface (✅ Bom - o que usamos!)

```kotlin
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor // interface
) : ViewModel() {
    fun pay() {
        paymentProcessor.processPayment(...)
    }
}
```

**Vantagens:**
- ✅ Desacoplado
- ✅ Testável
- ✅ Flexível
- ✅ Reutilizável

---

## 10. Perguntas Frequentes

### P: Por que Settings é um `object` e não uma classe normal?

**R:** Porque precisamos de uma **única instância global** acessível de qualquer lugar. Usar `object` garante:
- Única instância (singleton)
- Thread-safe automaticamente
- Sem necessidade de injeção de dependência para leitura

### P: Por que usar `data class` em vez de `class` normal?

**R:** `data class` gera automaticamente:
- `equals()`, `hashCode()`, `toString()`
- `copy()` para criar cópias modificadas
- Desestruturação

Exemplo:
```kotlin
val payment = Payment(1, 100.0, PaymentType.CREDIT, null)
val modified = payment.copy(amount = 200.0) // cria cópia com novo valor
```

### P: Por que `Payment` usa `val` e não `var`?

**R:** Para garantir **imutabilidade**. Uma vez criado, um `Payment` não pode ser modificado. Se precisar mudar algo, use `copy()`:
```kotlin
val newPayment = oldPayment.copy(amount = 150.0)
```

Isso evita bugs de mutação acidental.

### P: Quando usar `interface` e quando usar `sealed class`?

**Interface:**
- Quando múltiplas classes não relacionadas podem implementar
- Exemplo: `PaymentProcessor` (pode ter MSitef, PagSeguro, etc.)

**Sealed class:**
- Quando você quer uma hierarquia fechada de tipos
- Exemplo: `Result` (só pode ser Success, Error ou Loading)

### P: O que é `Flow<Result<T>>`?

**R:** É um padrão de streams reativos do Kotlin:

```kotlin
interface SettingsRepository {
    fun saveSetting(setting: Setting): Flow<Result<Unit>>
}

// Uso:
settingsRepository.saveSetting(setting).collect { result ->
    when (result) {
        is Result.Success -> println("Salvo!")
        is Result.Error -> println("Erro: ${result.error}")
        is Result.Loading -> println("Salvando...")
    }
}
```

- `Flow` = stream de valores ao longo do tempo
- `Result` = encapsula sucesso/erro/loading
- `<T>` = tipo genérico do dado

---

## 11. Checklist de Implementação

Se você for criar um módulo domain parecido no seu outro app:

- [ ] Criar módulo `:domain` (pure Kotlin, sem dependências Android)
- [ ] Criar `data class` para modelos de dados
- [ ] Criar `enum class` para valores fixos (tipos, estados)
- [ ] Criar `sealed class Result<T>` para encapsular resultados
- [ ] Criar `object Settings` para configurações globais
- [ ] Criar `interface` para contratos (repositories, processors)
- [ ] Criar módulos de implementação que dependem do `:domain`
- [ ] Usar injeção de dependência (Koin, Hilt, Dagger) para injetar implementações

---

## 12. Recursos Adicionais

### Conceitos Kotlin:
- [Data Classes](https://kotlinlang.org/docs/data-classes.html)
- [Object Declarations (Singleton)](https://kotlinlang.org/docs/object-declarations.html#object-declarations-overview)
- [Interfaces](https://kotlinlang.org/docs/interfaces.html)
- [Sealed Classes](https://kotlinlang.org/docs/sealed-classes.html)
- [Enum Classes](https://kotlinlang.org/docs/enum-classes.html)

### Arquitetura:
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android App Architecture](https://developer.android.com/topic/architecture)

---

**Conclusão:** O módulo domain é o **núcleo limpo** do seu app, definindo o que pode ser feito sem se preocupar com detalhes de implementação. Isso torna seu código mais testável, flexível e reutilizável! 🚀
