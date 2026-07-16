import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "tech.jitendhull.torrnado"
    compileSdk = 35

    defaultConfig {
        applicationId = "tech.jitendhull.torrnado"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }

            val keystoreFilePath = System.getenv("RELEASE_KEYSTORE_FILE")
                ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_FILE")?.toString()
                ?: project.findProperty("RELEASE_KEYSTORE_FILE")?.toString()
            val keystorePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_PASSWORD")?.toString()
                ?: project.findProperty("RELEASE_KEYSTORE_PASSWORD")?.toString()
            val keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: keystoreProperties.getProperty("RELEASE_KEY_ALIAS")?.toString()
                ?: project.findProperty("RELEASE_KEY_ALIAS")?.toString()
            val keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: keystoreProperties.getProperty("RELEASE_KEY_PASSWORD")?.toString()
                ?: project.findProperty("RELEASE_KEY_PASSWORD")?.toString()

            if (keystoreFilePath != null && keystorePassword != null && keyAlias != null && keyPassword != null) {
                storeFile = file(keystoreFilePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }
            val keystoreFilePath = System.getenv("RELEASE_KEYSTORE_FILE")
                ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_FILE")?.toString()
                ?: project.findProperty("RELEASE_KEYSTORE_FILE")?.toString()
            val keystorePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_PASSWORD")?.toString()
                ?: project.findProperty("RELEASE_KEYSTORE_PASSWORD")?.toString()
            val keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: keystoreProperties.getProperty("RELEASE_KEY_ALIAS")?.toString()
                ?: project.findProperty("RELEASE_KEY_ALIAS")?.toString()
            val keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: keystoreProperties.getProperty("RELEASE_KEY_PASSWORD")?.toString()
                ?: project.findProperty("RELEASE_KEY_PASSWORD")?.toString()

            if (keystoreFilePath != null && keystorePassword != null && keyAlias != null && keyPassword != null && file(keystoreFilePath).exists()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit & OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)

    // Jsoup
    implementation(libs.jsoup)

    // DataStore
    implementation(libs.datastore.preferences)

    // Testing
    testImplementation(libs.junit)

    // Material Kolor
    implementation(libs.material.kolor)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
