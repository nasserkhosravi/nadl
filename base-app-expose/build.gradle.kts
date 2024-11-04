plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.nasser.baseapp.expose"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = false
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation(project(mapOf("path" to ":myLibraryApi")))


}