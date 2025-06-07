plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.nasser.appexporter"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.nasser.appexporter"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        androidResources {
            this.localeFilters += listOf("en", "fa")
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {

//    to prevent R conflict value generation between base and exported, The build need to a module that have all R values (base-app-expose)
    implementation(project(mapOf("path" to ":base-app-expose")))
    implementation(project(mapOf("path" to ":myLibraryImpl")))
//    implementation("androidx.drawerlayout:drawerlayout:1.0.0")


}