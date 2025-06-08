plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

android {
    namespace = "io.nasser.appexporter"
    compileSdk = 35

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.2"
//    }
}
dependencies {

    //to prevent R conflict value generation between base and exported, The build need to a module that have all R values (base-app-expose)
    //but it make our plugin apk fat, Its not acceptable.
//    implementation(project(mapOf("path" to ":host-app-impl")))

    implementation(project(mapOf("path" to ":plugin-contract")))
    implementation(project(mapOf("path" to ":pluing-basic-impl")))
    implementation(project(mapOf("path" to ":plugin-compose-impl")))
}

