plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.tatalaraydar.nmedia"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.netology.nmedia"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    val core_version = "1.15.0"
    val appcompat_version = "1.7.0"
    val mdc_version = "1.12.0"
    val constraintlayout_version = "2.2.0"
    val recyclerview_version = "1.3.2"
    val junit_version = "4.13.2"
    val ext_junit_version = "1.2.1"
    val espresso_core_version = "3.6.1"
    val activity_version = "1.9.3"
    val lifecycle_version = "2.8.7"
    val gson_version = "2.11.0"


    implementation ("androidx.core:core-ktx:$core_version")
    implementation ("androidx.appcompat:appcompat:$appcompat_version")
    implementation ("com.google.android.material:material:$mdc_version")
    implementation ("androidx.constraintlayout:constraintlayout:$constraintlayout_version")
    implementation ("androidx.recyclerview:recyclerview:$recyclerview_version")
    implementation ("androidx.activity:activity-ktx:$activity_version")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation ("com.google.code.gson:gson:$gson_version")


    testImplementation ("junit:junit:$junit_version")
    androidTestImplementation ("androidx.test.ext:junit:$ext_junit_version")
    androidTestImplementation ("androidx.test.espresso:espresso-core:$espresso_core_version")
}