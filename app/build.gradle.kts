plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" // <-- AGREGA versión aquí
}

android {
    namespace = "com.example.app_clinica_atl"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.app_clinica_atl"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
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
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.foundation)
    implementation(libs.foundation)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //librerias nuevas
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    // Material icons (necesarios para Visibility / VisibilityOff)
    implementation("androidx.compose.material:material-icons-extended")

    // Room (SQLite) - runtime y extensiones KTX
    implementation("androidx.room:room-runtime:2.6.1")    // <-- NUEVO
    implementation("androidx.room:room-ktx:2.6.1")        // <-- NUEVO

    // Compilador de Room vía KSP
    ksp("androidx.room:room-compiler:2.6.1")               // <-- NUEVO

    //cargar imagenes (para mostrarlas en la UI)
    implementation("io.coil-kt:coil-compose:2.7.0")

    //Data Store Preferencia
    implementation("androidx.datastore:datastore-preferences:1.1.1")





    // ==== AGREGADOS PARA REST ====
    // Retrofit base
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // <-- NUEVO
    // Convertidor JSON con Gson
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // <-- NUEVO
    // OkHttp y logging interceptor
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // <-- NUEVO
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // <-- NUEVO








    //librerias para test locales (funcionalidades)
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.robolectric:robolectric:4.13")

    //test de implementacion de UI (AndroidTest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")

    implementation("androidx.core:core-splashscreen:1.0.1")


}
