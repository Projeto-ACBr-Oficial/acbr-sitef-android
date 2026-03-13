# Documentação — acbr-sitef-android

> 👋 **Primeira vez aqui?** Comece lendo **[COMO_COMEÇAR.md](./COMO_COMECCAR.md)** — um guia rápido de 5 minutos!

---

## 🚀 Começando (Recomendado)

Se você está **começando do zero**, siga esta ordem:

1. **[domain-module.md](./domain-module.md)** — Conceitos fundamentais (data class, interface, object singleton)
2. **[koin-integration.md](./koin-integration.md)** — 🟢 Padrão recomendado para DI
3. **[msitef-module.md](./msitef-module.md)** — Como integrar o módulo M-SiTef
4. **[print/README.md](../print/README.md)** — Como integrar impressoras (Print/Sunmi)
5. **[msitef-hilt-migration.md](./msitef-hilt-migration.md)** — Se você já usa Hilt (opcional)

---

## 📚 Documentação Completa

| Documento | Descrição | Quando usar |
|---|---|---|
| **[domain-module.md](./domain-module.md)** | Módulo domain: conceitos (data class, interface, object singleton, sealed class), arquitetura, Settings, pagamento | Entender fundamentos |
| **[koin-integration.md](./koin-integration.md)** | 🟢 **Koin (Padrão)**: guia completo de Dependency Injection, arquitetura, módulos, testes | 🎯 **Novos projetos** |
| **[msitef-module.md](./msitef-module.md)** | M-SiTef: arquitetura, classes, fluxo, dependências, exemplos com Koin | Integrar M-SiTef |
| **[../print/README.md](../print/README.md)** | Print: interface agnóstica, modelos (TextPrint, ImageData), implementações (Sunmi) | Integrar impressora |
| **[../print/sunmi/README.md](../print/sunmi/README.md)** | Sunmi Print: integração com impressoras Sunmi V2/P2/M2 | Usar Sunmi |
| **[msitef-hilt-migration.md](./msitef-hilt-migration.md)** | Opção A (Koin+Hilt) ou Opção B (Hilt nativo) para apps com Hilt | 🟡 Apps Hilt existentes |
| **[msitef-publish-as-lib.md](./msitef-publish-as-lib.md)** | Publicar módulo como `.aar` via Maven Local / GitHub Packages | Distribuir biblioteca |

---

## ⚡ Quick Start

### Novo projeto (Recomendado com Koin)

```kotlin
// 1. Adicionar dependências
dependencies {
    implementation(project(":domain"))
    implementation(project(":fiserv:msitef"))
    implementation(project(":print:sunmi"))
    implementation("io.insert-koin:koin-android:3.5.0")
}

// 2. Criar Application
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                msitefModule,
                sunmiPrinterModule
            )
        }
        Settings.updateSetting(Setting(EMPRESA_SITEF, "00000000"))
    }
}

// 3. Usar em ViewModel
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor,    // M-SiTef
    private val printRepository: PrintRepository       // Sunmi
) : ViewModel() {
    fun pay(amount: Double) {
        paymentProcessor.processPayment(payment, callback)
        // depois...
        printRepository.printText(receiptLines)
    }
}
```

### App com Hilt existente

Veja [msitef-hilt-migration.md](./msitef-hilt-migration.md) — **Opção A** (15 min) ou **Opção B** (1-2 h).

---

## 🎯 Fluxo de Arquitetura

```
APP (Activities, ViewModels)
    ↓ depende de
DOMAIN (Interfaces, Models)
    ↑ implementado por
MSITEF (M-SiTef)
    ↑ implementado por
PRINT:SUNMI (Impressoras Sunmi)
    ↓ usa
FISERV M-SITEF + SUNMI HARDWARE
```

---

## 📋 Recomendações por Cenário

| Cenário | Recomendação |
|---|---|
| Novo projeto do zero | 1. domain-module.md → 2. koin-integration.md → 3. msitef-module.md → 4. print/README.md |
| Integrar M-SiTef | msitef-module.md (seção 7) |
| Integrar impressora | print/README.md + print/sunmi/README.md |
| App com Hilt pronto | Começar por msitef-hilt-migration.md (Opção A) |
| Migrar para Hilt nativo | msitef-hilt-migration.md (Opção B) |
| Publicar como biblioteca | msitef-publish-as-lib.md |
| Entender conceitos Kotlin | domain-module.md (seção 2) |

---

## 🟢 Padrão Recomendado: Koin

Para **novos projetos**, recomendamos usar **Koin** como DI:

✅ Setup rápido (~5 min)  
✅ Sintaxe simples e idiomática  
✅ Performance otimizada  
✅ Excelente para modularização  
✅ Fácil de testar

👉 Veja [koin-integration.md](./koin-integration.md)

Se você já tem **Hilt**, veja [msitef-hilt-migration.md](./msitef-hilt-migration.md).

---

## 💡 Conceitos-Chave

- **data class**: Classes para transportar dados (imutáveis)
- **interface**: Contrato que define "o quê", não "como"
- **object (Singleton)**: Instância única, thread-safe, lazy-initialized
- **sealed class**: Hierarquia fechada de tipos
- **Dependency Injection**: Fornecer dependências externamente
- **Settings (Singleton)**: Configurações globais acessíveis em qualquer lugar
- **PrintRepository**: Interface agnóstica para impressoras

Detalhes em [domain-module.md](./domain-module.md).

---

## 🔗 Links Úteis

- [Koin Official](https://insert-koin.io)
- [Android Dependency Injection](https://developer.android.com/training/dependency-injection)
- [Kotlin Official](https://kotlinlang.org)
- [Sunmi Official](https://www.sunmi.com)

---

**Última atualização:** 2026-03-12  
**Status:** ✅ Documentação completa e padronizada



