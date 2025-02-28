plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
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
            manifestPlaceholders["usesCleartextTraffic"] = false

            buildConfigField ("String", "BASEURL", "\"http://10.0.2.2:9999\"")
        }
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = true
            
            buildConfigField ("String", "BASEURL", "\"http://10.0.2.2:9999\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.transport.api)
    implementation(libs.transport.api)
    implementation(libs.transport.api)
    implementation(libs.androidx.swiperefreshlayout)


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
    val nav_version = "2.8.4"
    val room_version = "2.6.1"
    val firebase_version = "33.6.0"
    val play_services_base_version = "18.5.0"
    val okhttp_version = "4.12.0"
    val glide_version = "4.16.0"
    val retrofit_version = "2.11.0"
    val retrofitgson_version = "2.11.0"
    val okhttplogging_version = "4.12.0"
    val coroutines_version = "1.8.1"


    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttplogging_version")
    implementation ("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofitgson_version")
    implementation("androidx.core:core-ktx:$core_version")
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("com.google.android.material:material:$mdc_version")
    implementation("androidx.constraintlayout:constraintlayout:$constraintlayout_version")
    implementation("androidx.recyclerview:recyclerview:$recyclerview_version")
    implementation("androidx.activity:activity-ktx:$activity_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("com.google.code.gson:gson:$gson_version")
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation(platform("com.google.firebase:firebase-bom:$firebase_version"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.android.gms:play-services-base:$play_services_base_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation ("com.github.bumptech.glide:glide:$glide_version")
    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttplogging_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

    testImplementation("junit:junit:$junit_version")
    androidTestImplementation("androidx.test.ext:junit:$ext_junit_version")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso_core_version")
}