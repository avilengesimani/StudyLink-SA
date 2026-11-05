import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)

    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val propertiesFile = rootProject.file("local.properties")

    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use {
            load(it)
        }
    }
}

val supabaseUrl = localProperties.getProperty("SUPABASE_URL", "")
val supabasePublishableKey =
    localProperties.getProperty("SUPABASE_PUBLISHABLE_KEY", "")

val googleWebClientId =
    localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")

android {
    namespace = "com.example.studylinksa"

    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.studylinksa"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"$supabaseUrl\""
        )

        buildConfigField(
            "String",
            "SUPABASE_PUBLISHABLE_KEY",
            "\"$supabasePublishableKey\""
        )

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"$googleWebClientId\""
        )
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Supabase
    implementation(
        platform("io.github.jan-tennert.supabase:bom:3.5.0")
    )
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    // Ktor HTTP client
    implementation("io.ktor:ktor-client-android:3.0.3")

    // Google Sign-In using Credential Manager
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Room Database for offline storage
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")

    // Background offline synchronisation
    implementation("androidx.work:work-runtime-ktx:2.10.5")

    // Firebase Cloud Messaging
    implementation(platform("com.google.firebase:firebase-bom:35.1.0"))
    implementation("com.google.firebase:firebase-messaging")

    // Android compatibility
    coreLibraryDesugaring(
        "com.android.tools:desugar_jdk_libs:2.1.5"
    )
}