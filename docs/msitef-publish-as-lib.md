# Convertendo o módulo `:fiserv:msitef` em uma biblioteca AAR publicável
> **Objetivo:** empacotar o módulo como `.aar` distribuível via Maven Local, GitHub Packages ou Maven Central.
---
## 1. Estratégia geral
O módulo já é um `com.android.library`. Os passos são:
1. Publicar `:domain` como `jar` (é uma `java-library`).
2. Publicar `:fiserv:msitef` como `aar`.
3. Substituir `project(":domain")` por coordenadas Maven.
4. O consumidor adiciona apenas o `aar` no `build.gradle.kts`.
Estrutura de coordenadas recomendada:
```
com.mjtech:domain:1.0.0          → jar  (puro Kotlin/JVM)
com.mjtech:fiserv-msitef:1.0.0   → aar  (Android Library)
```
---
## 2. Publicar o `:domain`
### `domain/build.gradle.kts`
```kotlin
plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`           // adicionar
}
// ...existing code...
pub# Convertendo o módulo `:fiserv:msitef` em uma biblioteca AAR publicável
> **Objetivo:** empacon> **Objetivo:** empacotar o módulo comocom.mjtech"
            artifactId ---
## 1. Estratégia geral
O módulo já é um `com.android.library`. Os passos são:
1. Publicar `:domain` coGi##ubO módulo já é um `cma1. Publicar `:domain` como `jar` (é uma `java-library`).co2. Publicar `:fiserv:msitef` como `aar`.
3. Substituir `  3. Substituir `project(":domain")` por OR4.         //         password = System.getenv("GITHUB_TOKEstrutura de coordenadas recomendada:
```
com.mjtech:domain:1le```
com.mjtech:domain:1.0.0         -
co 3com.mjtech:fiserv-msitef:1.0.0   → aar  (Android Librarydl```
---
## 2. Publicar o `:domain`
### `domain/build.gradlli--ar##
 ### `domain/build.gradle.li```kotlin
p    `maven-publish`plugins       id("io    alias(libs.pluginco    alias(libs.plugins.kotlin.serialization      `maven-publish`           // adicionar
bl}
// ...existing code...
pub# Convertendom(copub# Convertendo o m? > **Objetivo:** empacon> **Objetivo:** empacotar o módulo comocom.mjtech"
 -m            artifactId ---
## 1. Estratégia geral
O módulo já é um `c  ## 1. Estratégia geral
O  O módulo já é um `c}
1. Pub}
```
Substituir a dependência de projeto por coordenada Maven:
```kotlin
// antes
implementation(project(":domain"))
// depois
implementation("com.mjtech:domain:1.0.0")
```
```bash
./gradlew :fiserv:msitef```
com.mjtech:domain:1le```
com.mjtech:domain:1.0.0         -
co 3com.mjtech:fiserv-msitef:1.0.0   → aar  (Android Librarydl```
---
## 2. Publicar o  {co  com.mjtech:domain:1.0.0  co 3com.mjtech:fiserv-msitef:1.0lo---
## 2. Publicar o `:domain`
### `domain/build.gradlli--ar##
 ##.g##dl### `domain/build.gradlliep ### `domain/build.gradntation("p    `maven-publish`plugins       idenbl}
// ...existing code...
pub# Convertendom(cmplementation("io.insert-koin:koin-android:3.5.0")
}
```
Remover do `settings.gradle.kts` do consumidor:
//`kpub# Convertendom(copma -m            artifactId ---
## 1. Estratégia geral
O módulo já é um `c  ## 1. Estratégia geral
O  O módulo j e## 1. Estratégia geral
O m?`O módulo já é ```progO  O módulo já é um `c}
1. Pub}
```
Substitel1. Pub}
```
Substituir a m.```
Su.dSuai```kotlin
// antes
implementation(project(":doech.fiserv.ms// antesmeimplemeef// depois
implem{ *; }
-keep interfimplemenmjtech.domain.payment.repository.PaymentProc``sor { *; }com.mjtech:domain:1le```
ch.com.mjtech:domain:1.0.0ryco 3com.mjtech:fiserv-ms```
---
## 6. CI/CD com GitHub Packages (opcional)
```yaml
# .github/workflows##ub## 2. Publicar o `:domain`
### `domain/build.gradlli--ar##
 ##.g##dl### `domain/bui ub### `domain/build.gradlli   ##.g##dl### `domain/build.gra  // ...existing code...
pub# Convertendom(cmplementation("io.insert-koin:koin-android:3.5.0")
}
```
Remover d .pub# Convertendom(cmpis}
```
Remover do `settings.gradle.kts` do consumidor:
//`kpub# ConvethubRect//`kpub# Convertendom(copma -m            artiUB_TOKEN }}
```
---
## 7. Checklist
- [ ] Adicionar `maven-O módulo já é um `cuiO  O módulo j e## 1. Estratégia geral
O m?` eO m?`O módulo já é ```progO  O mó[ 1. Pub}
```
Substiect(":domain")` por coordenada Maven em```
SuerSums```
Su- [ ] RodarSu./Su.dSuai```kotlinub// antes
impleme`
imple Rodimplem{ *; }
-keep interfimplemenmjtech.domain.payment.repository.Pma-keep inter nch.com.mjtech:domain:1.0.0ryco 3com.mjtech:fiserv-ms```
---
## 6. CI/CD com GitHub Packages (opcion) ---
## 6. CI/CD com GitHub Packages (ocat > /Users/victor2752/SQG/Projetos/acbr-sitef-android/docs/msitef-hilt-migration.md << 'DOCEOF'
# Implementando o módulo M-SiTef em um projeto com Hilt
> **Contexto:** o módulo `:fiserv:msitef` usa **Koin** internamente. Este guia mostra como integrá-lo em um app que já usa **Hilt** sem reescrever o módulo, e também apresenta a opção de migrar o módulo para Hilt nativamente.
---
## Opção A — Usar o módulo Koin dentro de um app Hilt (sem alterar o módulo)
Esta é a abordagem mais rápida. Koin e Hilt podem coexistir no mesmo processo Android.
O Hilt gerencia o grafo de dependências do seu app; o Koin gerencia apenas o `MSitefPaymentProcessor`.
### A.1 — Adicionar Koin ao projeto
No `build.gradle.kts` do app:
```kotlin
dependencies {
    // Hilt (já existente)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    // Koin — apenas para o módulo MSitef
    implementation("io.insert-koin:koin-android:3.5.0")
    implementa# Implementando o módulo M-SiTef em um projeto com Hilt
> **Contexto:** o módulo `:fiserv:msitiz> **Contexto:** o módulo `:fiserv:msitef` usa **Koin**s ---
## Opção A — Usar o módulo Koin dentro de um app Hilt (sem alterar o módulo)
Esta é a abordagem mais rápida. Koin e Hilt podem coexistir no mesmo processo Android.
O Hilt gerencia o grafo de dependências do seu app; o K  ##odEsta é a abordagem mais rápida. Koin e Hilt podem coexistir no mesmo processo A  O Hilt gerencia o grafo de dependências do seu app; o Koin gerencia apenas o `MSitefPa00### A.1 — Adicionar Koin ao projeto
No `build.gradle.kts` do app:
```kotlin
dependencies {
    // HiteNo `build.gradle.kts` do app:
```kot1"```kotlin
dependencies {
   ttdependenin    // Hilt (      implementation("com.go      kapt("com.google.dagger:hilt-android-compiler:2.51.1")00    // Koin — apenas para o módulo MSitef
    implemenpa    implementation("io.insert-koin:koin-and o    implementa# Implementando o módulo M-SiTef em um `k> **Contexto:** o módulo `:fiserv:msitiz> **Contexto:** o módulo `:la## Opção A — Usar o módulo Koin dentro de um app Hilt (sem alterar o módulo)
Esta é a abordagpaEsta é a abordagem mais rápida. Koin e Hilt podem coexistir no mesmo processo A  O Hilt gerencia o grafo de dependências do seu app; o K  ##odEsta é a abordagem mais atNo `build.gradle.kts` do app:
```kotlin
dependencies {
    // HiteNo `build.gradle.kts` do app:
```kot1"```kotlin
dependencies {
   ttdependenin    // Hilt (      implementation("com.go      kapt("com.google.dagger:hilt-android-compiler:2.51.1")00    // Koin — apenas am```kotlin
dependencies {
   padependenPa    // HiteNo  ```kot1"```kotlin
dependencies {
   ttd  dependencies {
  a   ttdependen      implemenpa    implementation("io.insert-koin:koin-and o    implementa# Implementando o módulo M-SiTef em um `k> **Contexto:** o módulo `:fiserv:msitiz> **C  Esta é a abordagpaEsta é a abordagem mais rápida. Koin e Hilt podem coexistir no mesmo processo A  O Hilt gerencia o grafo de dependências do seu app; o K  ##odEsta é a abordagem mais atNo `build.gradle.kts` do app:
```kotlin
dependencies {
    // HiteNo `build. A```kotlin
dependencies {
    // HiteNo `build.gradle.kts` do app:
```kot1"```kotlin
dependencies {
   ttdependenin    // Hilt (      implementation("com.go      kapt("com.google.dagger:hilt-android-compiler:2.51.1")00  ?odependen
|    // HiteNo? ```kot1"```kotlin
dependencies {
   ttdsedependencies {
 ??   ttdependen rdependencies {
   padependenPa    // HiteNo  ```kot1"```kotlin
dependencies {
   ttd  dependencies {
  a   ttdependen      implemenpa    implementationoi   padependenKodependencies {
   ttd  dependencies {
  a   ttã   ttd  depenr   a   ttdependen     at```kotlin
dependencies {
    // HiteNo `build. A```kotlin
dependencies {
    // HiteNo `build.gradle.kts` do app:
```kot1"```kotlin
dependencies {
   ttdependenin    // Hilt (      implementation("com.go      kapt("com.google.dagger:hilt-android-compiler:2.51.1")00  ?odependen
|    // HiteNo? ```kot1"```kotlin
dependencies {
   ttdsedependencies {
 ??   ttdependen rdependencies {
   padependenPa  //dependenar    // HiteNoesdependencies {
    // HiteNo `b:d    // HiteNo//```kot1"```kotlin
dependencies {
   ttds.dependencies {
      ttdependenHi|    // HiteNo? ```kot1"```kotlin
dependencies {
   ttdsedependencies {
 ??   ttdependen rdependencies {
   padependenPa    // Hiledependencies {
   ttdsedependenc)
   ttdsedepenat ??   ttdependen rdepco   padependenPa    // HiteNo  matdependencies {
   ttd  dependencies {
  a   ttmp   ttd  depenib  a   ttdependen     
`   ttd  dependencies {
  a   ttã   ttd  depenr   a   ttdependen     at```kotlini/  a   ttã   ttd  depgedependencies {
    // HiteNo `build. A```kotlin
dependenex    // HiteNomjdependencies {
    // HiteNo `bPa    // HiteNor
```kot1"```kotlin
dependencies {
   ttd.Mdependencies {
 es   ttdependengg|    // HiteNo? ```kot1"```kotlin
dependencies {
   ttdsedependencies {
 ??   ttdependen rdependencies {
   padependenPa  //depengedependencies {
   ttdsedependencne   ttdsedepenax ??   ttdependen rdepdu   padependenPa  //dependenar  t:    // HiteNo `b:d    // HiteNo//```kot1"```kotlin
dependn
dependencies {
   ttds.dependencies {
      ttdepti   ttds.depente      ttdependenHi|  ymdependencies {
   ttdsedependencies {
 ??   ttdepe``   ttdsedepenAn ??   ttdependen rdepoc   padependenPa    // Hiledepenma   ttdsedependenc)
   ttdsedepenat ??  r    ttdsedepenat ? d   ttd  dependencies {
  a   ttmp   ttd  depenib  a   ttdependen     
`   ttd  depeno   a   ttmp   ttd  dep i`   ttd  dependencies {
  a   ttã   ttd  depSi  a   ttã   ttd  depenj    // HiteNo `build. A```kotlin
dependenex    // HiteNomjdependencies {
    // HiteNo `bPa      dependenex    // HiteNomjdependsi    // HiteNo `bPa    // HiteNor
```ko
@```kot1"```kotlin
dependencies ladependencies {
 ss   ttd.Mdepen { es   ttdependengg|  ledependencies {
   ttdsedependencies {
 ??   ttdepe i   ttdsedepenym ??   ttdependen rdepay   padependenPa  //depengedepen R   ttdsedependencne   ttdsedepenax ??  atdependn
dependencies {
   ttds.dependencies {
      ttdepti   ttds.depente      ttdependenHi|  ymdependencies {
   ttdsedependencies {
 ??  cdependst   ttds.depenul      ttdepti   ttds.?    ttdsedependencies {
 ??   ttdepe``   ttdsedepenAn ??   ttdepent ??   ttdepe``   ttdstt   ttdsedepenat ??  r    ttdsedepenat ? d   ttd  dependencies {
  a   ttmp   ttd  depenib  a   ttdepenO_  a   ttmp   ttd  depenib  a   ttdependen     
`   ttd  depenog(`   ttd  depeno   a   ttmp   ttd  dep i`   ttat  a   ttã   ttd  depSi  a   ttã   ttd  depenj    // HiteNo etdependenex    // HiteNomjdependencies {
    // HiteNo `bPa      dependenex     B    // HiteNo `bPa      dependenex    ? ```ko
@```kot1"```kotlin
dependencies ladependencies {
 ss   ttd.Mdepen { es   ttdependeri@``` vdependencies ladeor ss   ttd.Mdepen { es   ttdeod   ttdsedependencies {
 ??   ttdepe i   ttdsedepenym ?nt ??   ttdepe i   ttds|
dependencies {
   ttds.dependencies {
      ttdepti   ttds.depente      ttdependenHi|  ymdependencies {
   ttdsedependencies {
 ??  nt   ttds.depen|       ttdepti   ttds.ul   ttdsedependencies {
 ??  cdependst   ttds.depenul      ttdepto  ??  cdependst   ttdsge ??   ttdepe``   ttdsedepenAn ??   ttdepent ??   ttdepe``   ttdstt   ttds r  a   ttmp   ttd  depenib  a   ttdepenO_  a   ttmp   ttd  depenib  a   ttdependen     
`   ttd  depenog(`   ttd  depeno   a   tt|
`   ttd  depenog(`   ttd  depeno   a   ttmp   ttd  dep i`   ttat  a   ttã   ttd  dão     // HiteNo `bPa      dependenex     B    // HiteNo `bPa      dependenex    ? ```ko
@```kot1"```kotlin
dependencies ladependencies {
 ss   ttd.Mdepen { es   ttdepeo @```kot1"```kotlin
dependencies ladependencies {
 ss   ttd.Mdepen { es   ttdependeriuadependencies ```kot ss   ttd.Mdepen { es   ttdefe ??   ttdepe i   ttdsedepenym ?nt ??   ttdepe i   ttds|
dependencies {
   ttds.dependencies {
      ttdepti   ttadependencies {
   ttds.dependencies {
    prefs[stringPr   ttds.depenEM      ttdepti   ttds.     ttdsedependencies {
 ??  nt   ttds.depen|       ttdepti   ttd   ??  nt   ttds.depen|ri ??  cdependst   ttds.depenul      ttdepto  ??  cdependst   ttdsgete`   ttd  depenog(`   ttd  depeno   a   tt|
`   ttd  depenog(`   ttd  depeno   a   ttmp   ttd  dep i`   ttat  a   ttã   ttd  dão     // HiteNo `bPa      dependenex     B    // HiteNo `bPa      dependenex    ? ```ko
@```kot1"`r `   ttd  depenog(`   ttd  depeno   a   ttpp@```kot1"```kotlin
dependencies ladependencies {
 ss   ttd.Mdepen { es   ttdepeo @```kot1"```kotlin
dependencies ladependencies {
 ss   ttd.Mdepen { es   ttdependeriuadepeVidependencies ladect ss   ttd.Mdepen { es   ttde`Sdependencies ladependencies {
 ss   ttd.Mdepen {  d ss   ttd.Mdepen { es   ttde ]dependencies {
   ttds.dependencies {
      ttdepti   ttadependencies {
   ttds.dependencies {
    prefs[stringPr   ttds.depenEM      ttdeptPa   ttds.depenr`      ttdepti   ttader    ttds.dependencies {
    prefsê    prefs[stringPr   `  ??  nt   ttds.depen|       ttdepti   ttd   ??  nt   ttds.depen|ri ??  cdepen` `   ttd  depenog(`   ttd  depeno   cat > /Users/victor2752/SQG/Projetos/acbr-sitef-android/docs/README.md << 'DOCEOF'
# Documentação — acbr-sitef-android
| Documento | Descrição |
|---|---|
| [msitef-module.md](./msitef-module.md) | Documentação funcional completa do módulo `:fiserv:msitef`: arquitetura, classes, fluxo, dependências e exemplos de uso com Koin |
| [msitef-publish-as-lib.md](./msitef-publish-as-lib.md) | Guia para converter o módulo em uma biblioteca `.aar` publicável via Maven Local / GitHub Packages |
| [msitef-hilt-migration.md](./msitef-hilt-migration.md) | Guia para integrar o módulo em um projeto que usa Hilt, com duas opções: coexistência Koin+Hilt ou migração nativa para Hilt |
