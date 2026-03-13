# Módulo Print — Documentação Completa

> **Versão:** 1.0 · **Data:** 2026-03-12  
> **Namespace:** `com.mjtech.print`  
> **Tipo:** Módulo de impressão (multiple implementations)

---

## 📋 Índice

1. [O que é este módulo?](#1-o-que-é-este-módulo)
2. [Arquitetura Geral](#2-arquitetura-geral)
3. [Estrutura do Módulo](#3-estrutura-do-módulo)
4. [Interface PrintRepository](#4-interface-printrepository)
5. [Implementações Disponíveis](#5-implementações-disponíveis)
6. [Modelos de Dados](#6-modelos-de-dados)
7. [Como Usar (Com Koin)](#7-como-usar-com-koin)
8. [Fluxo de Impressão](#8-fluxo-de-impressão)
9. [Padrão Recomendado](#9-padrão-recomendado)
10. [Perguntas Frequentes](#10-perguntas-frequentes)

---

## 1. O que é este módulo?

O módulo `:print` é um **container de implementações de impressoras** para diferentes hardwares.

Ele fornece:
- ✅ **Interface unificada** (`PrintRepository`) para todas as impressoras
- ✅ **Implementações específicas** por tipo de hardware (Sunmi, Star, Zebra, etc)
- ✅ **Modelos de dados** únicos (TextPrint, ImageData, etc)
- ✅ **Zero acoplamento** ao tipo de impressora
- ✅ **Fácil extensão** para novos hardwares

### Por que separar o Print?

```
┌─────────────────────────────────────────────────────┐
│  APP MODULE                                         │
│  ├─ UI (Activities, ViewModels)                    │
│  └─ depende de → DOMAIN (PrintRepository)         │
└─────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────┐
│  DOMAIN MODULE (Pure Kotlin)                        │
│  └─ Interface PrintRepository (agnóstico)          │
└─────────────────────────────────────────────────────┘
              ↑
┌──────────────────────┬──────────────────────────────┐
│ PRINT:SUNMI          │ PRINT:STAR (futuro)         │
│ Implementação Sunmi  │ Implementação Star Micronics│
└──────────────────────┴──────────────────────────────┘
```

**Benefício:** Trocar de impressora sem mexer no app!

---

## 2. Arquitetura Geral

```
:domain
├── print/
│   ├── model/
│   │   ├── TextPrint.kt         ← data class
│   │   ├── TextStyle.kt         ← data class
│   │   └── ImageData.kt         ← data class
│   └── repository/
│       └── PrintRepository.kt   ← interface

:print
├── sunmi/               ← implementação para Sunmi
│   ├── SunmiPrinterManager
│   ├── SunmiPrinterRepository   ← implementa PrintRepository
│   ├── Mappers.kt
│   └── SunmiPrinterModule.kt    ← módulo Koin
│
└── (star/)             ← implementação futura para Star
    └── StarPrinterRepository
```

---

## 3. Estrutura do Módulo

```
print/
├── sunmi/                  ← sub-módulo Sunmi
│   ├── README.md           ← documentação Sunmi
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   ├── consumer-rules.pro
│   │
│   └── src/main/java/com/mjtech/print/
│       ├── di/
│       │   └── SunmiPrinterModule.kt
│       ├── data/
│       │   ├── source/
│       │   │   └── SunmiPrinterManager.kt
│       │   ├── repository/
│       │   │   └── SunmiPrinterRepository.kt
│       │   └── common/
│       │       └── Mappers.kt
│       └── AndroidManifest.xml
```

---

## 4. Interface PrintRepository

Contrato unificado para todas as implementações de impressora.

```kotlin
interface PrintRepository {
    
    /**
     * Imprime um texto simples em uma linha
     */
    fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>>
    
    /**
     * Imprime múltiplas linhas de texto
     */
    fun printText(linesText: List<TextPrint>): Flow<Result<Unit>>
    
    /**
     * Imprime um código QR
     */
    fun printQrCode(text: String): Flow<Result<Unit>>
    
    /**
     * Imprime uma imagem/bitmap
     */
    fun printBitmap(imageData: ImageData): Flow<Result<Unit>>
}
```

### Como implementar uma nova impressora?

1. Criar novo módulo `:print:novaimpressora`
2. Implementar `PrintRepository`
3. Criar módulo Koin
4. Usar normalmente no app!

Exemplo para Star Micronics:

```kotlin
// print/star/src/main/java/com/mjtech/print/data/repository/StarPrinterRepository.kt
internal class StarPrinterRepository(
    private val starManager: StarPrinterManager
) : PrintRepository {
    override fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>> = flow {
        try {
            // implementação Star específica
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro Star: ${e.message}"))
        }
    }
    // ... outros métodos
}
```

---

## 5. Implementações Disponíveis

### 5.1 Sunmi (Atual)

**Status:** ✅ Implementado  
**Modelos:** V2, P2, M2, TM2, TM7, TM8  
**Recursos:** Texto, QR Code, Imagem  

📖 **Documentação:** [print/sunmi/README.md](./sunmi/README.md)

```kotlin
// Adicionar ao app
dependencies {
    implementation(project(":print:sunmi"))
}

// Usar
startKoin {
    modules(sunmiPrinterModule)
}

// Injetar
class MyViewModel(
    private val printRepository: PrintRepository
) : ViewModel()
```

### 5.2 Star Micronics (Futuro)

**Status:** 🔴 Não implementado  
**Modelos:** StarMicronics SM-S210i, SM-L200, etc  

### 5.3 Zebra (Futuro)

**Status:** 🔴 Não implementado  
**Modelos:** ZD410, ZD420, etc  

---

## 6. Modelos de Dados

### `TextPrint` — Para textos

```kotlin
data class TextPrint(
    val text: String,        // "Obrigado pela compra!"
    val style: TextStyle?    // alinhamento, fonte, etc
)
```

**Exemplo:**
```kotlin
TextPrint(
    text = "Loja ACBR",
    style = TextStyle(style = "CENTER")
)
```

### `TextStyle` — Estilos de texto

```kotlin
data class TextStyle(
    val style: String  // "BOLD", "CENTER", "LEFT", "RIGHT"
)
```

### `ImageData` — Para imagens

```kotlin
data class ImageData(
    val data: ByteArray,      // bytes da imagem
    val width: Int,           // 384 para Sunmi
    val height: Int,          // proporcional
    val format: String        // "PNG", "JPEG"
)
```

**Exemplo:**
```kotlin
val logoBytes = context.assets.open("logo.png").readBytes()
ImageData(
    data = logoBytes,
    width = 384,
    height = 200,
    format = "PNG"
)
```

---

## 7. Como Usar (Com Koin)

### 7.1 Setup na Application

```kotlin
import android.app.Application
import com.mjtech.print.di.sunmiPrinterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@MyApplication)
            modules(sunmiPrinterModule)  // Sunmi
            // modules(starPrinterModule)  // Star (quando disponível)
        }
    }
}
```

### 7.2 Injetar e Usar em ViewModel

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.print.repository.PrintRepository
import com.mjtech.domain.print.model.TextPrint
import com.mjtech.domain.common.Result
import kotlinx.coroutines.launch

class ReceiptViewModel(
    private val printRepository: PrintRepository
) : ViewModel() {
    
    fun printReceipt() {
        viewModelScope.launch {
            val receipt = listOf(
                TextPrint("=== RECIBO ===", null),
                TextPrint("Data: 12/03/2026", null),
                TextPrint("Valor: R$ 100,00", null),
            )
            
            printRepository.printText(receipt).collect { result ->
                when (result) {
                    is Result.Success -> println("✅ Impresso!")
                    is Result.Error -> println("❌ ${result.error}")
                    is Result.Loading -> println("⏳ Imprimindo...")
                }
            }
        }
    }
}
```

---

## 8. Fluxo de Impressão

```
┌─────────────────────────────────────────┐
│ Usuário clica "Imprimir"                │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ ViewModel.printReceipt()                │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ printRepository.printText(receipt)      │  ← interface do domain
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ SunmiPrinterRepository.printText()      │  ← implementação
│ ├─ obtém impressora                    │
│ ├─ converte modelos                    │
│ └─ executa SDK                         │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Sunmi Printer SDK                       │
│ └─ comunica com hardware                │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Flow emite Result (Success/Error)       │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ ViewModel recebe resultado              │
│ ├─ onSuccess → atualizar UI            │
│ └─ onError → mostrar erro               │
└─────────────────────────────────────────┘
```

---

## 9. Padrão Recomendado

### Para novos projetos:

1. **Escolha a implementação** (Sunmi, Star, etc)
2. **Adicione dependência** do módulo `:print:xxx`
3. **Inicialize Koin** com o módulo
4. **Injetar `PrintRepository`** como dependência
5. **Use a interface** (agnóstico de hardware)

### Benefícios:

✅ **Desacoplamento** — trocar impressora sem mexer no app  
✅ **Reutilizabilidade** — código funciona em qualquer impressora  
✅ **Testabilidade** — mockar `PrintRepository` nos testes  
✅ **Manutenibilidade** — lógica centralizada em um módulo  

### Exemplo: Trocar de Sunmi para Star

**Antes (acoplado):**
```kotlin
// ❌ ruim — acoplado a Sunmi
class MyViewModel {
    private val sunmiPrinter = SunmiPrinterManager()  // hard-coded!
}
```

**Depois (desacoplado):**
```kotlin
// ✅ bom — agnóstico
class MyViewModel(
    private val printRepository: PrintRepository  // qualquer impressora!
) : ViewModel()
```

Trocar para Star:
```kotlin
// Só mudar no startKoin:
startKoin {
    // modules(sunmiPrinterModule)  // remover
    modules(starPrinterModule)      // adicionar
}
// App funciona normalmente! ✅
```

---

## 10. Perguntas Frequentes

### P: Posso usar múltiplas impressoras ao mesmo tempo?

**R:** ✅ Sim, criando bindings nomeados:

```kotlin
val multiPrinterModule = module {
    single<PrintRepository>(named("sunmi")) { 
        SunmiPrinterRepository(get()) 
    }
    single<PrintRepository>(named("star")) { 
        StarPrinterRepository(get()) 
    }
}

// Usar:
class MyViewModel(
    @Qualifier("sunmi") private val sunmiPrinter: PrintRepository,
    @Qualifier("star") private val starPrinter: PrintRepository
) : ViewModel()
```

### P: Como testar impressão sem hardware?

**R:** Mockar a interface:

```kotlin
// Test
class MockPrintRepository : PrintRepository {
    override fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>> = flow {
        emit(Result.Success(Unit))  // sempre sucesso em testes
    }
    // ... outros métodos
}

// Setup teste
startKoin {
    modules(module {
        single<PrintRepository> { MockPrintRepository() }
    })
}
```

### P: O que fazer se a impressora não está pronta?

**R:** O repositório retorna `Result.Error`:

```kotlin
printRepository.printText(receipt).collect { result ->
    when (result) {
        is Result.Error -> {
            // Tratar erro
            showDialog("Impressora não pronta: ${result.error}")
        }
        // ...
    }
}
```

### P: Posso customizar a implementação?

**R:** ✅ Sim, estendendo a classe:

```kotlin
class CustomSunmiPrinterRepository(
    sunmiManager: SunmiPrinterManager
) : SunmiPrinterRepository(sunmiManager) {
    
    override fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>> = flow {
        // sua lógica custom
        super.printSimpleText(textPrint)
    }
}
```

---

**Status:** ✅ Documentação Completa  
**Última atualização:** 2026-03-12  
**Versão:** 1.0

---

**👉 Para documentação específica:**
- Sunmi: [print/sunmi/README.md](./sunmi/README.md)
- Star: (em breve)
- Zebra: (em breve)

