import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = getLocalPropertiesVariables()

android {
    compileSdk = 35
    namespace = "br.gohan.cifrafinder"

    signingConfigs {
        create("release") {
            // Use local.properties for local builds, env vars for CI/CD
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS")
                ?: System.getenv("BITRISEIO_ANDROID_KEYSTORE_ALIAS")
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD")
                ?: System.getenv("BITRISEIO_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD")
            storeFile = file(
                localProperties.getProperty("RELEASE_STORE_FILE")
                    ?: (System.getenv("HOME") ?: ".") + "/release.jks"
            )
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD")
                ?: System.getenv("BITRISEIO_ANDROID_KEYSTORE_PASSWORD")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        disable.add("Instantiatable")
    }

    defaultConfig {
        applicationId = "br.gohan.cifrafinder"
        minSdk = 23
        targetSdk = 35
        versionCode = 14
        versionName = "14.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
        vectorDrawables.useSupportLibrary = true

        manifestPlaceholders["redirectSchemeName"] = "spotify-sdk"
        manifestPlaceholders["redirectHostName"] = "auth"

        buildConfigField(
            "String",
            "SPOTIFY_CLIENT_ID",
            localProperties.getProperty("SPOTIFY_CLIENT_ID")
                ?: "\"${System.getenv("SPOTIFY_CLIENT_ID") ?: ""}\""
        )
        buildConfigField(
            "String",
            "SERP_API_KEY",
            localProperties.getProperty("SERP_API_KEY")
                ?: "\"${System.getenv("SERP_API_KEY") ?: ""}\""
        )
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.work.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Compose
    val firebaseBom = platform(libs.firebase.bom)
    implementation(firebaseBom)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    androidTestImplementation(composeBom)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Spotify App
    implementation(libs.spotify.auth)

    // Retrofit
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit)

    // Tests
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}

fun getLocalPropertiesVariables(): Properties {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    return localProperties
}