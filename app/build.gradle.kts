plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.appdistribution)
}

android {
    namespace = "com.example.parcial_sebastiangranoblesardila"
    // SE ACTUALIZA A 36 PARA COMPATIBILIDAD CON ANDROIDX CORE 1.17.0
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.parcial_sebastiangranoblesardila"
        minSdk = 24
        // SE ACTUALIZA A 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // === GESTIÓN DE FIREBASE ===
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-ai")

    // === ANDROIDX Y LIBRERÍAS DE UI ===
    implementation(libs.androidx.core.ktx) // Esta es la que requiere SDK 36
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // COMPOSE BOM
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // OTROS
    implementation("io.coil-kt:coil-compose:2.6.0")

    // === DEPENDENCIAS DE TEST ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}