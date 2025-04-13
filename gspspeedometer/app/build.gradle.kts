plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms)
}

apply(plugin = "com.google.gms.google-services")

android {
    namespace = "com.example.speedometer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.speedometer"
        minSdk = 23
        targetSdk = 35
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

    buildFeatures {
        viewBinding = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    // Firebase & Google
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.maps) // ✅ Maps
    implementation(libs.maps.utils) // ✅ Correctly named maps utils


    // MPAndroidChart
    implementation(libs.mpandroidchart)

    // AndroidX ve Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
