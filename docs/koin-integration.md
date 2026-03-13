# Koin — Guia de Integração (Padrão Recomendado)

> **Versão:** 1.0 · **Data:** 2026-03-12  
> **Status:** 🟢 **Padrão recomendado para novos projetos**  
> **Namespace:** `org.koin`

---

## 📋 Índice

1. [O que é Koin?](#1-o-que-é-koin)
2. [Por que usar Koin?](#2-por-que-usar-koin)
3. [Conceitos Fundamentais](#3-conceitos-fundamentais)
4. [Arquitetura com Koin](#4-arquitetura-com-koin)
5. [Configuração Inicial](#5-configuração-inicial)
6. [Criando Módulos](#6-criando-módulos)
7. [Injeção de Dependências](#7-injeção-de-dependências)
8. [Exemplo Prático Completo](#8-exemplo-prático-completo)
9. [Testes com Koin](#9-testes-com-koin)
10. [Perguntas Frequentes](#10-perguntas-frequentes)

---

## 1. O que é Koin?

**Koin** é um framework de **Injeção de Dependência (Dependency Injection — DI)** puro para Kotlin.

Diferente de outras soluções:
- ✅ **100% Kotlin** (sem anotações de compilação)
- ✅ **Sem reflection pesada** (otimizado para performance)
- ✅ **Lightweight** (~100 KB)
- ✅ **Ideal para Android** (especialmente modular)
- ✅ **Fácil de aprender** (sintaxe simples e idiomática)

### Comparação com alternativas

| Aspecto | Koin | Hilt | Manual DI |
|---|---|---|---|
| **Setup** | ⚡ 5 min | ⏱️ 20 min | ⚠️ Complexo |
| **Curva de aprendizado** | 📈 Baixa | 📈 Média | 📈 Alta |
| **Performance** | ✅ Ótima | ✅ Excelente | ✅ Ótima |
| **Modularização** | ✅ Excelente | ✅ Boa | ⚠️ Manual |
| **Verificação compilação** | ⚠️ Runtime | ✅ Compilação | ✅ Runtime |
| **Overhead** | 📦 Mínimo | 📦 Médio | 📦 Nenhum |
| **Recomendado para** | 🟢 Novos projetos | 🟡 Apps Google | 🔴 Simples |

---

## 2. Por que usar Koin?

### O Problema sem DI

Sem Koin, você precisa criar instâncias manualmente em toda parte:

```kotlin
// ❌ Sem DI — acoplado e difícil de testar
class CheckoutActivity : AppCompatActivity() {
    private val settingsRepository = SettingsRepository()
    private val paymentProcessor = MSitefPaymentProcessor(this)
    
    fun pay() {
        paymentProcessor.processPayment(payment, callback)
    }
}

// ❌ Se precisar trocar MSitefPaymentProcessor por outra implementação,
//    precisa editar CheckoutActivity manualmente!
```

### Solução com Koin

```kotlin
// ✅ Com Koin — desacoplado e testável
class CheckoutActivity : AppCompatActivity() {
    private val paymentProcessor: PaymentProcessor by inject()
    
    fun pay() {
        paymentProcessor.processPayment(payment, callback)
    }
}

// ✅ Se precisar trocar implementação, só muda no módulo Koin!
//    Nenhuma mudança na Activity!
```

### Benefícios

1. **Desacoplamento** → Componentes não conhecem suas dependências
2. **Testabilidade** → Fácil substituir implementações em testes
3. **Modularização** → Cada módulo gerencia suas dependências
4. **Manutenibilidade** → Mudanças centralizadas em um só lugar
5. **Reusabilidade** → Mesmas instâncias são reutilizadas (singletons)

---

## 3. Conceitos Fundamentais

### 3.1 Injeção de Dependência

**Conceito:** Em vez de uma classe criar suas dependências, elas são **fornecidas externamente**.

```kotlin
// ❌ Sem injeção (hard-coded)
class UserViewModel {
    private val repository = UserRepository()  // cria aqui
}

// ✅ Com injeção (recebe como parâmetro)
class UserViewModel(
    private val repository: UserRepository  // recebe como parâmetro
)
```

### 3.2 Container de DI

Um **Container** (ou **Koin Context**) é um objeto que armazena todas as definições de como criar instâncias:

```
┌──────────────────────────────────────┐
│   KOIN CONTEXT (Container)           │
│                                      │
│  ┌──────────────────────────────┐   │
│  │ Module: AppModule            │   │
│  │ - PaymentProcessor → single  │   │
│  │ - Settings → singleton       │   │
│  └──────────────────────────────┘   │
│                                      │
│  ┌──────────────────────────────┐   │
│  │ Module: MSitefModule         │   │
│  │ - Activity Proxy → factory   │   │
│  └──────────────────────────────┘   │
│                                      │
└──────────────────────────────────────┘
        ↓
   Quando você pede uma dependência com inject(),
   Koin procura no container como criá-la
```

### 3.3 Escopo de Instância

Koin oferece diferentes "tempos de vida" para instâncias:

| Escopo | Comportamento | Uso |
|---|---|---|
| **`single {}`** | Uma única instância por app | Configurações, Settings, DB |
| **`factory {}`** | Nova instância a cada injeção | Objetos descartáveis |
| **`scoped {}`** | Uma instância por escopo (ex: Activity) | ViewModels de tela |
| **`singleOf()`** | Single usando construtor Kotlin | Simples e idiomático |
| **`factoryOf()`** | Factory usando construtor Kotlin | Simples e idiomático |

```kotlin
module {
    single { Settings }                    // uma instância única
    factory { MSitefPaymentProcessor(get()) }  // nova a cada vez
    scoped { CheckoutViewModel(get()) }   // uma por Activity
}
```

### 3.4 Módulos

Um **módulo** agrupa definições de DI relacionadas:

```kotlin
// domain/di/DomainModule.kt
val domainModule = module {
    single { Settings }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

// msitef/di/MSitefModule.kt
val msitefModule = module {
    single<PaymentProcessor> { MSitefPaymentProcessor(get()) }
}

// app/di/AppModule.kt
val appModule = module {
    single { UserRepository(get()) }
}
```

Depois usa todos juntos:

```kotlin
startKoin {
    modules(
        domainModule,
        msitefModule,
        appModule
    )
}
```

---

## 4. Arquitetura com Koin

### 4.1 Estrutura de camadas

```
┌─────────────────────────────────────────────────────┐
│  APP MODULE                                         │
│  ├─ Activities, ViewModels, Composables             │
│  ├─ Repositories (implementação)                    │
│  └─ di/AppModule.kt ← gerencia DI local            │
└─────────────────────────────────────────────────────┘
              ↓ depende de
┌─────────────────────────────────────────────────────┐
│  DOMAIN MODULE (Pure Kotlin)                        │
│  ├─ Interfaces (PaymentProcessor, Repository)      │
│  ├─ Data classes (Payment, Settings, etc)          │
│  └─ Lógica de negócio pura                         │
└─────────────────────────────────────────────────────┘
              ↑ implementa
┌─────────────────────────────────────────────────────┐
│  MSITEF MODULE (Android Library)                    │
│  ├─ MSitefPaymentProcessor                         │
│  ├─ Activity Proxy                                  │
│  └─ di/MSitefModule.kt ← expõe dependências       │
└─────────────────────────────────────────────────────┘
```

### 4.2 Fluxo de inicialização

```
1. Application.onCreate()
        ↓
2. startKoin {
        ├─ androidContext(this)
        └─ modules(appModule, msitefModule, ...)
        }
        ↓
3. Koin Container criado
        ├─ Single instances: Settings, DB, etc
        └─ Factories: ViewModels, Presenters, etc
        ↓
4. Activity/Fragment inicia
        ↓
5. Activity pede: val repository by inject()
        ↓
6. Koin encontra no container e fornece
```

---

## 5. Configuração Inicial

### 5.1 Adicionar dependência

No `build.gradle.kts` do app:

```kotlin
dependencies {
    // Koin for Jetpack Compose + Android
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")
    
    // OU se usar ViewModel
    implementation("io.insert-koin:koin-androidx-viewmodel:3.5.0")
}
```

### 5.2 Criar classe Application

```kotlin
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            // 1. Fornecer contexto Android
            androidContext(this@MyApplication)
            
            // 2. Carregar todos os módulos
            modules(
                appModule,
                msitefModule,
                // ... mais módulos
            )
        }
    }
}
```

### 5.3 Declarar no AndroidManifest

```xml
<application
    android:name=".MyApplication"
    ...>
```

---

## 6. Criando Módulos

### 6.1 Módulo simples

```kotlin
// di/AppModule.kt
import org.koin.dsl.module
import com.example.app.repository.UserRepository

val appModule = module {
    // Single: uma instância para todo app
    single { UserRepository() }
    
    // Factory: nova instância a cada injeção
    factory { UserUseCase(get()) }
}
```

### 6.2 Módulo com interfaces

```kotlin
// di/RepositoryModule.kt
import org.koin.dsl.module

val repositoryModule = module {
    // Implementação de interface
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PaymentProcessor> { MSitefPaymentProcessor(get()) }
}
```

### 6.3 Módulo com ViewModel

```kotlin
// di/ViewModelModule.kt
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // ViewModels automaticamente com ciclo de vida
    viewModel { CheckoutViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
```

### 6.4 Qualificadores (nomes)

Quando você tem múltiplas implementações da mesma interface:

```kotlin
// ❌ Sem qualificador: conflito!
single<Logger> { LoggerImpl() }
single<Logger> { FirebaseLogger() }  // erro!

// ✅ Com qualificador: diferencia
single<Logger>(named("local")) { LoggerImpl() }
single<Logger>(named("firebase")) { FirebaseLogger() }

// Injetar:
class MyClass(
    @Qualifier("local") val logger: Logger  // outra forma
) {
    // ...
}

// Ou assim:
val logger1: Logger by inject(named("local"))
val logger2: Logger by inject(named("firebase"))
```

---

## 7. Injeção de Dependências

### 7.1 Em Activities/Fragments

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import org.koin.android.ext.android.inject
import com.example.app.repository.UserRepository

class MainActivity : ComponentActivity() {
    
    // Lazy injection — criada quando acessada
    private val userRepository: UserRepository by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Usar repositório
        val users = userRepository.getUsers()
    }
}
```

### 7.2 Em ViewModels

```kotlin
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CheckoutViewModel : ViewModel(), KoinComponent {
    
    // Injetar no ViewModel via construtor (preferível)
    constructor(
        private val paymentProcessor: PaymentProcessor,
        private val settingsRepository: SettingsRepository
    ) : super()
    
    // OU via Koin (menos idiomático)
    private val paymentProcessor: PaymentProcessor by inject()
}
```

### 7.3 Em Classes normais

```kotlin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyPresenter : KoinComponent {
    private val repository: UserRepository by inject()
    
    fun loadUsers() {
        repository.getUsers()
    }
}
```

### 7.4 Com get()

Para injeção manual dentro de módulos:

```kotlin
val appModule = module {
    single { SettingsRepository() }
    single { UserRepository(get()) }  // pega SettingsRepository acima
    single { UserUseCase(get()) }     // pega UserRepository
}
```

---

## 8. Exemplo Prático Completo

### 8.1 Estrutura do projeto

```
app/
├── Application.kt          ← inicia Koin
├── AndroidManifest.xml     ← declara Application
├── di/
│   ├── AppModule.kt        ← módulo do app
│   └── ViewModelModule.kt  ← módulo de ViewModels
└── ui/
    ├── checkout/
    │   ├── CheckoutActivity.kt
    │   ├── CheckoutViewModel.kt
    │   └── CheckoutComposable.kt
    └── settings/
        └── SettingsViewModel.kt

domain/  ← módulo separado
├── repository/
│   ├── SettingsRepository.kt (interface)
│   ├── PaymentProcessor.kt (interface)
│   └── model/ ...
└── model/
    ├── Payment.kt
    ├── Settings.kt
    └── ...
```

### 8.2 Código de exemplo

**1. Interfaces do Domain:**

```kotlin
// domain/repository/PaymentProcessor.kt
interface PaymentProcessor {
    fun processPayment(payment: Payment, callback: PaymentCallback)
}

// domain/repository/SettingsRepository.kt
interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
}
```

**2. Implementações:**

```kotlin
// app/data/repository/SettingsRepositoryImpl.kt
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override suspend fun getSettings(): Settings {
        // implementar...
    }
}
```

**3. Módulos Koin:**

```kotlin
// app/di/AppModule.kt
import org.koin.dsl.module
import org.koin.core.context.startKoin

val appModule = module {
    // Repositories
    single<SettingsRepository> { 
        SettingsRepositoryImpl(get())  // DataStore é injetado
    }
    
    // Use cases / Interactors
    factory { GetSettingsUseCase(get()) }
}

// app/di/ViewModelModule.kt
import org.koin.androidx.viewmodel.dsl.viewModel

val viewModelModule = module {
    viewModel { 
        CheckoutViewModel(
            paymentProcessor = get(),
            settingsRepository = get()
        )
    }
}
```

**4. Application:**

```kotlin
// app/MyApplication.kt
import android.app.Application
import com.mjtech.fiserv.msitef.di.msitefModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule,
                viewModelModule,
                msitefModule  // do módulo M-SiTef
            )
        }
    }
}
```

**5. ViewModel:**

```kotlin
// app/ui/checkout/CheckoutViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    fun loadSettings() = viewModelScope.launch {
        val settings = settingsRepository.getSettings()
        // atualizar UI
    }
    
    fun pay(amount: Double) {
        val payment = Payment(
            id = System.currentTimeMillis(),
            amount = amount,
            type = PaymentType.CREDIT
        )
        paymentProcessor.processPayment(payment, object : PaymentCallback {
            override fun onSuccess(id: String, msg: String?) {
                // atualizar UI
            }
            override fun onFailure(code: String, msg: String) {
                // mostrar erro
            }
            override fun onCancelled(msg: String?) {
                // usuário cancelou
            }
        })
    }
}
```

**6. Activity:**

```kotlin
// app/ui/checkout/CheckoutActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : ComponentActivity() {
    private val viewModel: CheckoutViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CheckoutScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun CheckoutScreen(viewModel: CheckoutViewModel) {
    Button(onClick = { viewModel.pay(100.00) }) {
        Text("Pagar R$ 100,00")
    }
}
```

---

## 9. Testes com Koin

### 9.1 Teste unitário simples

```kotlin
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock

class CheckoutViewModelTest {
    
    @Before
    fun setup() {
        startKoin {
            modules(
                module {
                    single { mock<PaymentProcessor>() }
                    single { mock<SettingsRepository>() }
                }
            )
        }
    }
    
    @After
    fun tearDown() {
        stopKoin()
    }
    
    @Test
    fun testPayment() {
        val viewModel = CheckoutViewModel(
            get(),  // PaymentProcessor mock
            get()   // SettingsRepository mock
        )
        
        // testar...
    }
}
```

### 9.2 Teste com módulo fake

```kotlin
val fakeModule = module {
    single<PaymentProcessor> { FakePaymentProcessor() }
    single<SettingsRepository> { FakeSettingsRepository() }
}

class CheckoutViewModelTest {
    @Before
    fun setup() {
        startKoin {
            modules(fakeModule)
        }
    }
}
```

---

## 10. Perguntas Frequentes

### P: Qual é a diferença entre `single` e `factory`?

**R:**
- **`single`** → Cria UMA instância que é reutilizada sempre
- **`factory`** → Cria uma NOVA instância a cada injeção

```kotlin
module {
    single { Settings }           // sempre mesma instância
    factory { UserPresenter() }   // cria nova a cada vez
}
```

**Use `single` para:** Configurações, banco de dados, repositories com estado  
**Use `factory` para:** ViewModels, Presenters, objetos sem estado

---

### P: O que é `scoped`?

**R:** Uma instância por "escopo" (ex: por Activity, por Fragment).

```kotlin
module {
    scoped { CheckoutViewModel(get()) }  // uma por Activity
}

// Em Activity:
private val viewModel: CheckoutViewModel by inject()
// Mesma Activity → mesma instância
// Outra Activity → outra instância
```

---

### P: Posso usar Koin com Hilt?

**R:** ✅ Sim! Veja [msitef-hilt-migration.md](./msitef-hilt-migration.md) para detalhes.

**Opção A:** Koin + Hilt coexistindo (recomendado se você já tem Hilt)  
**Opção B:** Migrar para Hilt nativo (para grafo único)

---

### P: Como testar com Koin?

**R:** Crie um módulo fake e reinicie Koin:

```kotlin
@Before
fun setup() {
    stopKoin()  // parar o Koin anterior
    startKoin {
        modules(testModule)  // módulo com mocks
    }
}

@After
fun tearDown() {
    stopKoin()
}
```

---

### P: Koin é seguro para múltiplas threads?

**R:** ✅ Sim! Koin é **thread-safe**.

```kotlin
// Seguro chamar de qualquer thread
val userRepository = get<UserRepository>()  // thread-safe
```

---

### P: Qual versão de Koin usar?

**R:** Use a versão mais recente:

```kotlin
implementation("io.insert-koin:koin-android:3.5.0")  // ✅ current
```

Verifique em [https://github.com/InsertKoinIO/koin/releases](https://github.com/InsertKoinIO/koin/releases)

---

## Próximos passos

- 📖 Leia [msitef-module.md](./msitef-module.md) para integrar o módulo M-SiTef
- 📖 Leia [domain-module.md](./domain-module.md) para conceitos de domínio
- 🧪 Teste com [Koin Testing](https://insert-koin.io/docs/reference/koin-test/)
- 🔗 Veja documentação oficial em [https://insert-koin.io](https://insert-koin.io)

---

**Status:** 🟢 Padrão recomendado — use Koin para novos projetos!

Se você tem um app com **Hilt**, veja [msitef-hilt-migration.md](./msitef-hilt-migration.md).

