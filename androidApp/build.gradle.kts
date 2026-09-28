plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.fungorn.trainingcapacity"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.fungorn.trainingcapacity"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.feature.dashboard)
    implementation(projects.feature.form)
    implementation(projects.feature.mesocycles)

    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.datastore.preferences.core)
    implementation(libs.decompose)
    implementation(libs.mvikotlin)

    debugImplementation(compose.uiTooling)
}
