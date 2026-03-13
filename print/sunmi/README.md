# Módulo Sunmi Print — Documentação Completa

> **Versão:** 1.0 · **Data:** 2026-03-12  
> **Namespace:** `com.mjtech.print`  
> **Tipo:** Android Library (Impressoras Sunmi)

---

## 📋 Índice

1. [O que é este módulo?](#1-o-que-é-este-módulo)
2. [Arquitetura](#2-arquitetura)
3. [Fluxo de Uma Impressão](#3-fluxo-de-uma-impressão)
4. [Classes e Responsabilidades](#4-classes-e-responsabilidades)
5. [Dependências do Módulo](#5-dependências-do-módulo)
6. [Configuração Inicial](#6-configuração-inicial)
7. [Como Usar no Seu App (com Koin) — Padrão Recomendado](#7-como-usar-no-seu-app-com-koin--padrão-recomendado)
8. [Modelos do Domínio](#8-modelos-do-domínio)
9. [Tipos de Impressão Suportados](#9-tipos-de-impressão-suportados)
10. [Codes de Erro Conhecidos](#10-códigos-de-erro-conhecidos)
11. [Perguntas Frequentes](#11-perguntas-frequentes)

---

## 1. O que é este módulo?

O módulo `:print:sunmi` é um **Android Library** que encapsula toda a lógica de integração com impressoras **Sunmi** (V2/P2/M2/TM2).

### Características Principais

- ✅ **Encapsulamento completo** do SDK Sunmi Printer
- ✅ **Interface limpa** (`PrintRepository`) para o restante da aplicação
- ✅ **Zero acoplamento** ao código do app (usa Dependency Injection)
- ✅ **Suporte a múltiplos tipos de impressão** (texto, QR Code, imagem)
- ✅ **Tratamento de erros** padronizado
- ✅ **Flow-based** para operações assíncronas
- ✅ **Suporte a Koin** (injeção de dependência recomendada)

### Vantagem Arquitetural

```
┌─────────────────────────────────────────────────────┐
│  APP MODULE                                         │
│  ├─ UI (Activities, ViewModels)                    │
│  └─ depende de → DOMAIN (PrintRepository)         │
└─────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────┐
│  DOMAIN MODULE (Pure Kotlin)                        │
│  └─ Interfaces (PrintRepository)                    │
│  └─ Models (TextPrint, ImageData)                   │
└─────────────────────────────────────────────────────┘
              ↑
┌─────────────────────────────────────────────────────┐
│  SUNMI PRINT MODULE (Android Library)               │
│  ├─ Implementa → PrintRepository                    │
│  ├─ Manager → SunmiPrinterManager (singleton)       │
│  ├─ Repository → SunmiPrinterRepository            │
│  └─ Mappers → Conversão de tipos                    │
└─────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────┐
│  SUNMI PRINTER SDK (Biblioteca Sunmi)               │
│  └─ Comunica com hardware da impressora             │
└─────────────────────────────────────────────────────┘
```

**Benefício:** Se você quiser trocar para outra marca de impressora (ex: Star Micronics, Zebra), basta criar um novo módulo que implemente `PrintRepository`. O app não precisa mudar nada!

---

## 2. Arquitetura

```
:domain              ← interfaces e modelos puros (sem Android)
    ├─ PrintRepository (interface)
    ├─ Result<T> (sealed class para resultados)
    └─ Models (TextPrint, ImageData, TextStyle)

:print:sunmi        ← implementação Sunmi (Android Library)
    ├─ SunmiPrinterManager    ← singleton para gerenciar impressora
    ├─ SunmiPrinterRepository ← implementa PrintRepository
    ├─ Mappers.kt            ← converte tipos (ImageData → Bitmap)
    ├─ SunmiPrinterModule.kt  ← módulo Koin
    └─ AndroidManifest.xml   ← permissões necessárias
```

---

## 3. Fluxo de Uma Impressão

```
┌────────────────────────────────────────────────────────────┐
│ 1. Usuário clica "Imprimir" no ReceiptActivity             │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 2. ViewModel chama:                                        │
│    printRepository.printSimpleText(textPrint)             │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 3. SunmiPrinterRepository:                                 │
│    ├─ obtém impressora de SunmiPrinterManager             │
│    ├─ converte TextPrint → Sunmi TextStyle                 │
│    ├─ executa printer.lineApi().printText(...)            │
│    └─ emit Result.Success ou Result.Error                 │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 4. Sunmi Printer SDK comunica com hardware                │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 5. Flow emite Result (Success ou Error)                    │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│ 6. ViewModel recebe resultado:                            │
│    ├─ onSuccess → atualizar UI ("Impressão OK")          │
│    └─ onError   → mostrar mensagem de erro                │
└────────────────────────────────────────────────────────────┘
```

---

## 4. Classes e Responsabilidades

### 4.1 `SunmiPrinterManager`

**Visibilidade:** `internal` (encapsulada no módulo)  
**Padrão:** Singleton thread-safe

**Responsabilidades:**
- Gerenciar conexão com impressora Sunmi
- Obter instância única da impressora
- Tratar callbacks do SDK Sunmi

```kotlin
// SunmiPrinterManager.kt
class SunmiPrinterManager private constructor(
    context: Context
) {
    private var sunmiPrinterInstance: Printer? = null
    
    init {
        try {
            PrinterSdk.getInstance().getPrinter(context, object : PrinterListen {
                override fun onDefPrinter(p: Printer?) {
                    sunmiPrinterInstance = p  // armazena instância
                }
                
                override fun onPrinters(list: MutableList<Printer?>?) {
                    // múltiplas impressoras
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }
    
    fun getPrinter(): Printer? = sunmiPrinterInstance
    
    companion object {
        @Volatile
        private var INSTANCE: SunmiPrinterManager? = null
        
        fun getInstance(context: Context): SunmiPrinterManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SunmiPrinterManager(context).also { INSTANCE = it }
            }
    }
}
```

**Características:**
- ✅ Thread-safe (usa `synchronized`)
- ✅ Lazy initialization
- ✅ Singleton pattern (Double-Checked Locking)

---

### 4.2 `SunmiPrinterRepository`

**Visibilidade:** `internal` (encapsulada no módulo)  
**Injetado por:** Koin como `PrintRepository`

**Implementa interface:**
```kotlin
interface PrintRepository {
    fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>>
    fun printText(linesText: List<TextPrint>): Flow<Result<Unit>>
    fun printQrCode(text: String): Flow<Result<Unit>>
    fun printBitmap(imageData: ImageData): Flow<Result<Unit>>
}
```

**Métodos:**

| Método | Descrição | Exemplo |
|---|---|---|
| `printSimpleText()` | Imprime um texto simples | "Obrigado pela compra" |
| `printText()` | Imprime múltiplas linhas | ["Loja:", "Produto: X", "Valor: R$ 10"] |
| `printQrCode()` | Imprime código QR | URL, ID de transação |
| `printBitmap()` | Imprime imagem | Logo da loja, comprovante visual |

**Exemplo:**
```kotlin
// SunmiPrinterRepository.kt
internal class SunmiPrinterRepository(
    private val sunmiPrinterManager: SunmiPrinterManager
) : PrintRepository {

    private val MAX_WIDTH: Int = 384  // largura máxima da impressora
    
    override fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>> = flow {
        val printer = sunmiPrinterManager.getPrinter()
        if (printer == null) {
            emit(Result.Error("Impressora não inicializada"))
            return@flow
        }
        try {
            val textStyle = TextStyle
                .getStyle()
                .setAlign(Align.CENTER)
            
            printer.lineApi().initLine(BaseStyle.getStyle().setAlign(Align.LEFT))
            printer.lineApi().printText(textPrint.text, textStyle)
            printer.lineApi().autoOut()  // ejetar papel
            
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao imprimir: ${e.message}"))
        }
    }
}
```

---

### 4.3 `Mappers.kt`

Converte tipos do domínio para tipos do SDK Sunmi.

```kotlin
// Mappers.kt
fun ImageData.toAndroidBitmap(): Bitmap? {
    val imageBytes = this.data
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

// Internamente redimensiona para não exceder MAX_WIDTH
private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
    if (bitmap.width <= maxWidth) {
        return bitmap
    }
    val newHeight = bitmap.height * maxWidth / bitmap.width
    return bitmap.scale(maxWidth, newHeight)
}
```

---

### 4.4 `SunmiPrinterModule.kt`

Módulo Koin que expõe as dependências.

```kotlin
// SunmiPrinterModule.kt
val sunmiPrinterModule = module {
    
    // Singleton do gerenciador (inicialização única)
    single<SunmiPrinterManager> { 
        SunmiPrinterManager.getInstance(get())  // context injetado
    }
    
    // Repository implementando interface do domain
    single<PrintRepository> { 
        SunmiPrinterRepository(get())  // SunmiPrinterManager injetado
    }
}
```

---

## 5. Dependências do Módulo

### 5.1 Módulos internos

```
:domain
```

### 5.2 Dependências externas (Gradle)

No `build.gradle.kts` do módulo `:print:sunmi`:

```kotlin
dependencies {
    implementation(project(":domain"))
    
    // Sunmi Printer SDK
    implementation("com.sunmi:printerx:1.1.17")
    
    // Koin (Injeção de Dependência)
    implementation("io.insert-koin:koin-android:3.5.0")
    
    // Android
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
}
```

### 5.3 Dependência de runtime

O módulo requer que o **hardware seja Sunmi** (V2, P2, M2, TM2, etc).

```
Vendor: Sunmi
Modelos: V2, P2, M2, TM2, TM7, TM8, etc
```

---

## 6. Configuração Inicial

### 6.1 Adicionar permissões no AndroidManifest

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.PRINT" />
```

### 6.2 Inicializar na Application

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@MyApplication)
            modules(sunmiPrinterModule)
        }
    }
}
```

---

## 7. Como Usar no Seu App (com Koin) — Padrão Recomendado

> ⚠️ **Importante:** Este é o padrão **recomendado** para novos projetos com impressoras Sunmi.

### 7.1 Adicionar dependências no `build.gradle.kts` do app

```kotlin
dependencies {
    implementation(project(":domain"))
    implementation(project(":print:sunmi"))
    
    // Koin para DI
    implementation("io.insert-koin:koin-android:3.5.0")
}
```

### 7.2 Criar `Application.kt`

```kotlin
import android.app.Application
import com.mjtech.print.di.sunmiPrinterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // ✅ Iniciar Koin com módulo Sunmi
        startKoin {
            androidContext(this@MyApplication)
            modules(sunmiPrinterModule)
        }
    }
}
```

### 7.3 Criar ViewModel com injeção

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.domain.print.model.TextPrint
import com.mjtech.domain.print.repository.PrintRepository
import com.mjtech.domain.common.Result
import kotlinx.coroutines.launch

class ReceiptViewModel(
    private val printRepository: PrintRepository  // injetado pelo Koin!
) : ViewModel() {
    
    fun printReceipt(receiptText: List<String>) {
        viewModelScope.launch {
            val textPrints = receiptText.map { 
                TextPrint(text = it, style = null) 
            }
            
            printRepository.printText(textPrints).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // ✅ Impressão OK
                        println("Comprovante impresso com sucesso!")
                    }
                    is Result.Error -> {
                        // ❌ Erro na impressão
                        println("Erro: ${result.error}")
                    }
                    is Result.Loading -> {
                        // ⏳ Imprimindo...
                        println("Imprimindo...")
                    }
                }
            }
        }
    }
    
    fun printQrCode(url: String) {
        viewModelScope.launch {
            printRepository.printQrCode(url).collect { result ->
                when (result) {
                    is Result.Success -> println("QR Code impresso!")
                    is Result.Error -> println("Erro: ${result.error}")
                    is Result.Loading -> println("Imprimindo QR Code...")
                }
            }
        }
    }
}
```

### 7.4 Usar ViewModel na Activity

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class ReceiptActivity : ComponentActivity() {
    
    private val receiptViewModel: ReceiptViewModel by koinViewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Button(onClick = { 
                receiptViewModel.printReceipt(listOf(
                    "=== COMPROVANTE ===",
                    "Data: 12/03/2026",
                    "Valor: R$ 100,00",
                    "Operador: VICTOR",
                    "==================="
                ))
            }) {
                Text("Imprimir Comprovante")
            }
        }
    }
}
```

---

## 8. Modelos do Domínio

### `TextPrint`

```kotlin
data class TextPrint(
    val text: String,        // texto a imprimir
    val style: TextStyle?    // estilo opcional (alinhamento, fonte, etc)
)
```

### `TextStyle`

```kotlin
data class TextStyle(
    val style: String  // estilos: "BOLD", "CENTER", "LEFT", "RIGHT", etc
)
```

### `ImageData`

```kotlin
data class ImageData(
    val data: ByteArray,      // bytes da imagem (PNG, JPEG)
    val width: Int,           // largura em pixels
    val height: Int,          // altura em pixels
    val format: String        // "PNG", "JPEG", etc
)
```

---

## 9. Tipos de Impressão Suportados

| Tipo | Descrição | Exemplo | Limitações |
|---|---|---|---|
| **Texto Simples** | Uma linha de texto | "Obrigado!" | Max 384px de largura |
| **Múltiplas Linhas** | Lista de textos | ["Linha 1", "Linha 2"] | Até 32 linhas |
| **QR Code** | Código QR automático | URL, ID transação | 90x90 até 300x300 |
| **Imagem/Bitmap** | Imagem em bytes | Logo, comprovante | Redimensionada para 384px |

---

## 10. Códigos de Erro Conhecidos

| Código | Descrição | Ação Recomendada |
|---|---|---|
| `"Impressora não inicializada"` | SDK não inicializou | Reinicie o app |
| `"Erro ao imprimir texto"` | Falha na impressão | Verifique papel/tinta |
| `"Erro ao imprimir QR Code"` | QR Code inválido | Validate dados de entrada |
| `"Erro ao imprimir imagem"` | Imagem corrompida | Verifique formato (PNG/JPEG) |
| `"Erro ao carregar imagem"` | Conversão failed | Redimensionar imagem |

---

## 11. Perguntas Frequentes

### P: Como verificar se a impressora está inicializada?

**R:** O repositório retorna `Result.Error` se a impressora for `null`:

```kotlin
if (printer == null) {
    emit(Result.Error("Impressora não inicializada"))
}
```

---

### P: Qual é o tamanho máximo de uma imagem?

**R:** Largura máxima: **384 pixels** (padrão Sunmi).  
Se maior, será redimensionada automaticamente mantendo proporção.

```kotlin
private val MAX_WIDTH: Int = 384

private fun scaleBitmap(bitmap: Bitmap): Bitmap {
    if (bitmap.width <= MAX_WIDTH) {
        return bitmap
    }
    val newHeight = bitmap.height * MAX_WIDTH / bitmap.width
    return bitmap.scale(MAX_WIDTH, newHeight)
}
```

---

### P: Posso imprimir múltiplas cópias?

**R:** ✅ Sim, chamando `printText()` múltiplas vezes:

```kotlin
fun printMultipleCopies(text: String, copies: Int) {
    viewModelScope.launch {
        repeat(copies) {
            printRepository.printText(
                listOf(TextPrint(text, null))
            ).collect { /* handle result */ }
        }
    }
}
```

---

### P: Como desativar a ejeção automática de papel?

**R:** Remova a chamada `printer.lineApi().autoOut()`:

```kotlin
// Com ejeção (padrão)
printer.lineApi().autoOut()

// Sem ejeção (comentado)
// printer.lineApi().autoOut()
```

---

### P: Qual é o tamanho máximo de um texto por linha?

**R:** Aproximadamente **32 caracteres** em fonte normal.  
Letras maisculas e fontes maiores ocupam menos espaço.

---

### P: Como formatar um QR Code personalizado?

**R:** O módulo usa configuração padrão (ErrorLevel.L, Dot=9).  
Para customizar, edite `SunmiPrinterRepository`:

```kotlin
val textStyle = QrStyle.getStyle()
    .setErrorLevel(ErrorLevel.H)  // H, M, Q, L
    .setDot(10)                    // tamanho do ponto
    .setAlign(Align.CENTER)

printer.lineApi().printQrCode(text, textStyle)
```

---

**Status:** ✅ Documentação Completa e Padronizada  
**Última atualização:** 2026-03-12  
**Versão:** 1.0

---

**👉 Próximo passo:** Adicione ao seu app em `Application.onCreate()` ✅

