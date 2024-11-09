plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.bossapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bossapp"
        minSdk = 24
        targetSdk = 34
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)

    // RecyclerView and CardView
    implementation(libs.androidx.recyclerview.v130)
    implementation(libs.androidx.cardview)

    // Firebase Libraries
    implementation(platform(libs.firebase.bom.v3220))
    implementation(libs.firebase.auth.ktx.v2211)
    implementation(libs.firebase.firestore.ktx.v2471)
    implementation(libs.firebase.analytics.ktx)

    // Material Components
    implementation(libs.material)
    implementation(libs.androidx.material3.v110)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom.v20240100))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.activity.compose.v180)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.ui.test.junit4)

    // Debugging tools
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
