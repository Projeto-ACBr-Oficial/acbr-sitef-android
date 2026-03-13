# M-SiTef com Hilt — Guia de Integração

> **Versão:** 1.0 · **Data:** 2026-03-12  
> **Contexto:** o módulo `:fiserv:msitef` usa **Koin** internamente. Este guia mostra como integrá-lo em um app que já usa **Hilt** sem reescrever o módulo (Opção A), ou migrar o módulo para Hilt nativamente (Opção B).

---

## 📋 Índice

1. [Quando usar cada abordagem?](#quando-usar-cada-abordagem)
2. [Opção A — Koin + Hilt Coexistindo](#opção-a--koin--hilt-coexistindo)
3. [Opção B — Migrar para Hilt Nativo](#opção-b--migrar-para-hilt-nativo)
4. [Comparação Rápida](#comparação-rápida)
5. [Configuração de Settings](#configuração-de-settings)
6. [Checklists de Implementação](#checklists-de-implementação)

---

## Quando usar cada abordagem?

| Cenário | Recomendação |
|---|---|
| Você tem um **app com Hilt** pronto em produção | **Opção A** (rápido, 15 min) |
| Você quer **minimizar mudanças** no módulo | **Opção A** |
| Você está **começando do zero** e quer um único DI | **Veja [msitef-module.md](./msitef-module.md)** (use Koin puro) |
| Você quer **grafo DI completamente unificado** | **Opção B** (1-2 h) |
| Seu projeto é **de longo prazo** com muitos devs | **Opção B** |

---

## Opção A — Koin + Hilt Coexistindo

✅ **Melhor para:** Integração rápida em apps Hilt já existentes  
⏱️ **Tempo:** ~15 minutos  
⚠️ **Trade-off:** Duas bibliotecas de DI no mesmo processo (overhead mínimo)

### A.1 — Adicionar Koin ao projeto

No `build.gradle.kts` do app:

```kotlin
dependencies {
    // Hilt (já existente)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Koin — apenas para o módulo MSitef
    implementation("io.insert-koin:koin-android:3.5.0")

    implementation(project(":domain"))
    implementation(project(":fiserv:msitef"))
}
```

### A.2 — Inicializar Koin na `Application`

```kotlin
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Koin separadamente — não conflita com Hilt
        startKoin {
            androidContext(this@MyApplication)
            modules(msitefModule)   // com.mjtech.fiserv.msitef.di.msitefModule
        }

        // Popular Settings do SiTef
        Settings.updateSetting(Setting(EMPRESA_SITEF,  "00000000"))
        Settings.updateSetting(Setting(ENDERECO_SITEF, "192.168.1.100"))
        Settings.updateSetting(Setting(OPERADOR,       "0001"))
        Settings.updateSetting(Setting(CNPJ_CPF,       "12345678000195"))
        Settings.updateSetting(Setting(CNPJ_AUTOMACAO, "12345678000195"))
    }
}
```

### A.3 — Expor `PaymentProcessor` para o grafo do Hilt

Crie um módulo Hilt que obtém a instância do Koin e a expõe como binding:

```kotlin
// di/PaymentModule.kt
@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    /**
     * Obtém o PaymentProcessor gerenciado pelo Koin e o expõe para o Hilt.
     * Isso evita reescrever o módulo msitef.
     */
    @Provides
    @Singleton
    fun providePaymentProcessor(): PaymentProcessor {
        return KoinPlatform.getKoin().get()
    }
}
```

### A.4 — Injetar normalmente com Hilt no ViewModel

```kotlin
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val paymentProcessor: PaymentProcessor   // provido pelo Hilt via Koin
) : ViewModel() {

    fun pay(amount: Double) {
        val payment = Payment(
            id = System.currentTimeMillis(),
            amount = amount,
            type = PaymentType.CREDIT,
            installmentDetails = null
        )
        paymentProcessor.processPayment(payment, object : PaymentCallback {
            override fun onSuccess(transactionId: String, message: String?) { /* ... */ }
            override fun onFailure(errorCode: String, errorMessage: String) { /* ... */ }
            override fun onCancelled(message: String?) { /* ... */ }
        })
    }
}
```

### A.5 — Injetar o ViewModel na Activity/Fragment

```kotlin
@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private val viewModel: CheckoutViewModel by viewModels()
}
```

### Prós e Contras da Opção A

| | |
|---|---|
| ✅ | Não altera o código do módulo `:fiserv:msitef` |
| ✅ | Migração rápida (~15 min) |
| ✅ | Koin e Hilt coexistem sem problemas |
| ⚠️ | Duas bibliotecas de DI no mesmo processo (overhead mínimo) |
| ⚠️ | `KoinPlatform.getKoin().get()` é menos idiomático |

---

## Opção B — Migrar para Hilt Nativo

✅ **Melhor para:** Projetos de longo prazo, grafo DI unificado  
⏱️ **Tempo:** ~1-2 horas  
⚠️ **Trade-off:** Requer alterar o módulo `:fiserv:msitef`

Reescreve o módulo `:fiserv:msitef` substituindo Koin por Hilt. Recomendado quando você quer um grafo de DI completamente unificado.

### B.1 — Adicionar Hilt ao módulo

`fiserv/msitef/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")   // adicionar
    kotlin("kapt")                         // adicionar
}

dependencies {
    implementation(project(":domain"))

    // Remover Koin:
    // implementation(libs.koin.android)

    // Adicionar Hilt:
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}
```

### B.2 — Criar o módulo Hilt (substitui `MSitefModule.kt` Koin)

```kotlin
// di/MSitefModule.kt
package com.mjtech.fiserv.msitef.di

import android.content.Context
import com.mjtech.domain.payment.repository.PaymentProcessor
import com.mjtech.fiserv.msitef.payment.MSitefPaymentProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MSitefModule {

    @Provides
    @Singleton
    fun providePaymentProcessor(
        @ApplicationContext context: Context
    ): PaymentProcessor = MSitefPaymentProcessor(context)
}
```

### B.3 — Alternativa com `@Binds` (mais idiomático)

Se preferir injeção por construtor, remova o `internal` de `MSitefPaymentProcessor` e adicione `@Inject`:

```kotlin
// MSitefPaymentProcessor.kt
class MSitefPaymentProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) : PaymentProcessor {
    // ...existing code...
}

// di/MSitefModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class MSitefModule {

    @Binds
    @Singleton
    abstract fun bindPaymentProcessor(
        impl: MSitefPaymentProcessor
    ): PaymentProcessor
}
```

### B.4 — `Application` sem Koin

```kotlin
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // NÃO chamar startKoin — não é mais necessário

        // Popular Settings do SiTef continua igual
        Settings.updateSetting(Setting(EMPRESA_SITEF,  "00000000"))
        Settings.updateSetting(Setting(ENDERECO_SITEF, "192.168.1.100"))
        Settings.updateSetting(Setting(OPERADOR,       "0001"))
        Settings.updateSetting(Setting(CNPJ_CPF,       "12345678000195"))
        Settings.updateSetting(Setting(CNPJ_AUTOMACAO, "12345678000195"))
    }
}
```

### B.5 — ViewModel com Hilt

```kotlin
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val paymentProcessor: PaymentProcessor
) : ViewModel() {
    // ...existing code...
}
```

### Prós e Contras da Opção B

| | |
|---|---|
| ✅ | Grafo de DI único e idiomático |
| ✅ | Verificação em tempo de compilação |
| ✅ | Remove Koin completamente |
| ⚠️ | Requer alterar o módulo `:fiserv:msitef` |
| ⚠️ | Requer `kapt` ou `ksp` no módulo |

---

## Comparação rápida

| Critério | Opção A (Koin + Hilt) | Opção B (Hilt nativo) |
|---|---|---|
| **Tempo de migração** | ~15 min | ~1-2 h |
| **Altera o módulo msitef** | Não | Sim |
| **DI unificado** | Não | Sim |
| **Verificação em compilação** | Parcial | Total |
| **Recomendado para** | Integração rápida | Projeto de longo prazo |
| **Custo de manutenção** | Baixo | Muito baixo |
| **Flexibilidade futura** | Média | Alta |

---

## Configuração de Settings independente de DI

O objeto `Settings` do domínio é um **singleton puro** (sem DI). Ele pode ser populado a partir de DataStore/SharedPreferences:

```kotlin
// Exemplo com DataStore Preferences
suspend fun loadSettingsFromDataStore(dataStore: DataStore<Preferences>) {
    dataStore.data.first().let { prefs ->
        prefs[stringPreferencesKey(EMPRESA_SITEF)]?.let {
            Settings.updateSetting(Setting(EMPRESA_SITEF, it))
        }
        prefs[stringPreferencesKey(ENDERECO_SITEF)]?.let {
            Settings.updateSetting(Setting(ENDERECO_SITEF, it))
        }
        prefs[stringPreferencesKey(OPERADOR)]?.let {
            Settings.updateSetting(Setting(OPERADOR, it))
        }
        prefs[stringPreferencesKey(CNPJ_CPF)]?.let {
            Settings.updateSetting(Setting(CNPJ_CPF, it))
        }
        prefs[stringPreferencesKey(CNPJ_AUTOMACAO)]?.let {
            Settings.updateSetting(Setting(CNPJ_AUTOMACAO, it))
        }
    }
}
```

> Chame essa função no `SplashViewModel` (ou equivalente) antes de qualquer pagamento.

---

## Checklists de Implementação

### Checklist — Opção A (Koin + Hilt)

- [ ] Adicionar `koin-android` ao `build.gradle.kts` do app
- [ ] Chamar `startKoin { modules(msitefModule) }` na `Application`
- [ ] Criar `PaymentModule` Hilt com `KoinPlatform.getKoin().get()`
- [ ] Anotar Activity/Fragment com `@AndroidEntryPoint`
- [ ] Anotar ViewModels com `@HiltViewModel` e `@Inject constructor`
- [ ] Popular `Settings` antes do primeiro pagamento
- [ ] Testar o fluxo de pagamento completo

### Checklist — Opção B (Hilt nativo)

- [ ] Adicionar `hilt-android` e `kapt` ao módulo `:fiserv:msitef`
- [ ] Substituir `MSitefModule.kt` Koin por módulo Hilt
- [ ] Anotar `MSitefPaymentProcessor` com `@Inject` ou usar `@Provides`
- [ ] Remover dependência do `koin-android` do módulo
- [ ] Remover `startKoin` da `Application`
- [ ] Popular `Settings` antes do primeiro pagamento
- [ ] Executar `./gradlew clean build` para verificar compilação
- [ ] Testar o fluxo de pagamento completo

---

## Próximos passos

- 📖 Veja [msitef-module.md](./msitef-module.md) para uma visão geral do módulo M-SiTef
- 📖 Veja [domain-module.md](./domain-module.md) para entender os conceitos fundamentais
- 🚀 Comece integrando com **Opção A** se você já tem Hilt, depois migre para **Opção B** se necessário

---

