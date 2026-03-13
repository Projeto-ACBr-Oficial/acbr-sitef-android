# Como Usar Esta Documentação — Guia Rápido

> **TL;DR** (Too Long; Didn't Read) — Versão resumida  
> Se você só tem 5 minutos, leia **esta página**!

---

## 🎯 Qual é a SUA Situação?

Escolha abaixo e siga o caminho recomendado:

### 1️⃣ Estou começando um NOVO projeto do zero

**Tempo:** ~2 horas para ler e entender toda a arquitetura  
**Recomendação:** Use **Koin** (padrão moderno e recomendado)

```
Ler na ordem:
  1. [domain-module.md](./domain-module.md)           (30 min) — fundamentos
  2. [koin-integration.md](./koin-integration.md)     (30 min) — DI recomendada
  3. [msitef-module.md](./msitef-module.md)          (1 h)   — integração M-SiTef
```

**Resultado:** Você terá um app modular, testável e escalável com Koin + M-SiTef.

---

### 2️⃣ Já tenho um app com **Hilt**, preciso integrar M-SiTef

**Tempo:** ~15 minutos + 5 min de código  
**Recomendação:** Use **Opção A** (Koin + Hilt coexistindo)

```
Ler em ordem:
  1. [msitef-hilt-migration.md](./msitef-hilt-migration.md) 
     → Seção "Quando usar cada abordagem?"
     → Seção "Opção A — Koin + Hilt Coexistindo"
     → Checklist Opção A
```

**Resultado:** Integração rápida sem reescrever nada no módulo M-SiTef.

---

### 3️⃣ Tenho um app com **Hilt** e quero um DI 100% unificado

**Tempo:** ~1-2 horas (requer alterações no módulo)  
**Recomendação:** Use **Opção B** (Migração para Hilt nativo)

```
Ler em ordem:
  1. [msitef-hilt-migration.md](./msitef-hilt-migration.md)
     → Seção "Opção B — Migrar para Hilt Nativo"
     → Reescrever o módulo :fiserv:msitef
     → Checklist Opção B
```

**Resultado:** Grafo de DI completamente unificado, verificação em compilação.

---

### 4️⃣ Preciso entender os CONCEITOS básicos (data class, interface, singleton)

**Tempo:** ~30 minutos  
**Recomendação:** Leia seção 2 de [domain-module.md](./domain-module.md)

```
Seções importantes:
  - 2.1 O que é uma data class?
  - 2.2 O que é uma interface?
  - 2.3 O que é um object (Singleton)?
  - 2.4 O que é um enum class?
  - 2.5 O que é uma sealed class?
```

**Resultado:** Entenderá os blocos de construção da arquitetura.

---

### 5️⃣ Preciso publicar o módulo como biblioteca `.aar`

**Tempo:** ~1 hora + automação CI/CD  
**Recomendação:** Leia [msitef-publish-as-lib.md](./msitef-publish-as-lib.md)

```
Ler na ordem:
  1. Estratégia geral
  2. Configurar Maven Local
  3. Publicar em GitHub Packages (ou Maven Central)
```

**Resultado:** Seu módulo `.aar` publicado para outros projetos usarem.

---

## 📚 Lista de Documentos

| Doc | Para quem? | Tempo | Prioridade |
|---|---|---|---|
| [domain-module.md](./domain-module.md) | Todos | 30 min | 🔴 Essencial |
| [koin-integration.md](./koin-integration.md) | Novos projetos | 30 min | 🔴 Essencial (se novo projeto) |
| [msitef-module.md](./msitef-module.md) | Integração M-SiTef | 1 h | 🟠 Importante |
| [msitef-hilt-migration.md](./msitef-hilt-migration.md) | Apps Hilt | 30 min | 🟡 Condicional |
| [msitef-publish-as-lib.md](./msitef-publish-as-lib.md) | AAR/Maven | 1 h | 🟡 Condicional |

---

## 🚦 Decisão Rápida: Koin vs Hilt

### Use **Koin** se:
- ✅ Você está começando um novo projeto
- ✅ Quer setup rápido (~5 min)
- ✅ Quer código simples e idiomático
- ✅ Quer fácil de testar

### Use **Hilt** se:
- ✅ Você já tem um projeto com Hilt
- ✅ Quer grafo de DI compilado (verificação em build time)
- ✅ Seu projeto é muito grande (100+ devs)

---

## ⚡ 10 Segundos: Quick Start Koin

```kotlin
// 1. build.gradle.kts
dependencies {
    implementation("io.insert-koin:koin-android:3.5.0")
}

// 2. Application.kt
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(msitefModule)
        }
    }
}

// 3. ViewModel.kt
class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor
) : ViewModel()

// 4. Pronto! Koin injeta tudo automaticamente!
```

---

## ❓ Perguntas Comuns

### P: Por onde eu começo?
**R:** Começando do zero? Leia [domain-module.md](./domain-module.md) primeiro.

### P: Qual é o melhor (Koin ou Hilt)?
**R:** Para novos projetos: **Koin** 🟢  
Para apps com Hilt: **Opção A** (Koin + Hilt coexistindo) 🟡

### P: Quanto tempo leva para integrar?
**R:** 
- Koin novo: ~2-3 horas
- Koin em app Hilt: ~15 minutos (Opção A)
- Hilt nativo: ~1-2 horas (Opção B)

### P: Preciso ler TUDO?
**R:** Não! Leia apenas o que é relevante para sua situação (veja "Qual é a SUA Situação?" acima).

### P: Qual versão está recomendada?
**R:** A mais recente:
- Koin: **3.5.0**
- Hilt: **2.51.1**

---

## 🔗 Links Diretos

- 🟢 **Novo projeto com Koin:** [koin-integration.md](./koin-integration.md)
- 🎯 **Integrar M-SiTef:** [msitef-module.md](./msitef-module.md)
- 🟡 **App Hilt + M-SiTef:** [msitef-hilt-migration.md](./msitef-hilt-migration.md)
- 📦 **Publicar como AAR:** [msitef-publish-as-lib.md](./msitef-publish-as-lib.md)
- 📖 **Fundamentos Kotlin:** [domain-module.md](./domain-module.md)

---

## ✅ Checklist de Leitura

Para novo projeto com Koin + M-SiTef:

- [ ] Lido [domain-module.md](./domain-module.md) — entendi data class, interface, singleton
- [ ] Lido [koin-integration.md](./koin-integration.md) — sei como usar Koin
- [ ] Lido [msitef-module.md](./msitef-module.md) — entendi fluxo de pagamento
- [ ] Implementei Application.kt com startKoin
- [ ] Implementei ViewModel injetando PaymentProcessor
- [ ] Testei fluxo de pagamento M-SiTef
- [ ] Pronto para produção! 🚀

---

## 🎓 Próximas Etapas

### Depois de ler a documentação:

1. **Implementar:** Crie seu app seguindo os exemplos práticos
2. **Testar:** Use as seções de FAQ para troubleshoot problemas comuns
3. **Otimizar:** Veja seções de testes para escrever testes unitários
4. **Deploy:** Considere publicar como biblioteca se houver reutilização

---

## 📞 Precisa de ajuda?

- 📖 Leia a seção **FAQ** de cada documento
- 🔍 Procure por palavras-chave relevantes (Ctrl+F)
- 🔗 Siga os links cruzados entre documentos
- ❓ Veja [CHANGELOG.md](./CHANGELOG.md) para ver mudanças recentes

---

**Status:** ✅ Documentação Completa e Padronizada  
**Última atualização:** 2026-03-12  
**Versão:** 1.0

---

**👉 Próximo passo:** Escolha sua situação acima e comece a ler!

