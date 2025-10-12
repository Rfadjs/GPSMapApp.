plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gpsmapapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gpsmapapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // 🔹 Librería necesaria para integrar y mostrar Google Maps dentro de la app
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // 🔹 Librería que permite acceder a la ubicación del dispositivo (GPS, red, Wi-Fi)
    //     Se utiliza junto con FusedLocationProviderClient para obtener la ubicación actual
    implementation("com.google.android.gms:play-services-location:21.0.1")
}