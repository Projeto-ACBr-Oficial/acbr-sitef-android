plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mjtech.fiserv.clisitef"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("jniLibs/armeabi-v7a/")
            jniLibs.srcDirs("jniLibs/arm64-v8a/")
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
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        jniLibs {
            keepDebugSymbols.add("**/libclisitef.so")
            useLegacyPackaging = true
        }

        resources {
            excludes.add("META-INF/*")
            excludes.add("META-INF/io.netty.versions.properties")
        }
    }
}

dependencies {

    implementation(files("./libs/clisitef-android.jar"))

    // Adicione aqui as bibliotecas compartilhadas referente ao terminal.
    // Exemplo das bibliotecas do GPOS780:
    //implementation(files("./libs/libppcomp-001.37-250509-gpos780-release.aar"))
    //implementation(files("./libs/libgedi-2.1.2-7384e13-gpos780Neo-payment-release.aar"))

    implementation(project(":domain"))
    implementation(project(":fiserv:base"))

    implementation(libs.koin.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}