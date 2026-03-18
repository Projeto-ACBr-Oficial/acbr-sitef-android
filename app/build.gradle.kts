plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mjtech.fintesthub.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mjtech.fintesthub.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Para trabalhar com os terminais GPOS e Positivo é necessário usar assinatura no app
        named("debug") { }
        create("gpos") {
            storeFile = file("Caminho\\.jks")
            storePassword = "storePassword"
            keyAlias = "keyAlias"
            keyPassword = "keyPassword"
        }
        create("positivo") {
            storeFile = file("Caminho\\.jks")
            storePassword = "storePassword"
            keyAlias = "keyAlias"
            keyPassword = "keyPassword"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isJniDebuggable = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = null
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // Configuração de flavors para diferenciar terminais que utilizam assinatura.
    flavorDimensions.add("brand")
    productFlavors {
        // Flavor padrão, sem customizações específicas
        create("standard") {
            dimension = "brand"
            isDefault = true
        }

        create("gpos") {
            dimension = "brand"
            applicationIdSuffix = ".gpos"
            versionNameSuffix = "-GPOS"
            signingConfig = signingConfigs.getByName("gpos")
        }

        create("positivo") {
            dimension = "brand"
            applicationIdSuffix = ".positivo"
            versionNameSuffix = "-POSITIVO"
            signingConfig = signingConfigs.getByName("positivo")
        }
    }
}

dependencies {

    // Módulo de domínio, onde ficam as regras de negócio e modelos de dados
    implementation(project(":domain"))

    // Módulo de integração com impressora em equipamentos Sunmi
    implementation(project(":print:sunmi"))

    // Módulos de integração com as soluções de pagamento da Fiserv
    implementation(project(":fiserv:base"))
    implementation(project(":fiserv:msitef"))
    implementation(project(":fiserv:clisitef"))

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.data.store)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}