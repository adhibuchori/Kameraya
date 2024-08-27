plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.android.detekt)
    id("kotlin-kapt")
}

android {
    namespace = "com.adhibuchori.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "API_KEY", "\"6f8856ed-9189-488f-9011-0ff4b6c08edc\"")

        buildConfigField("String", "BASE_URL", "\"https://mymarket-api.phincon.site\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Module
    implementation(project(":domain"))

    // Koin
    implementation(libs.koin.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Data Store
    implementation(libs.androidx.datastore.preferences)

    // Logging Interceptor and Okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Firebase BoM
    api(platform(libs.firebase.bom))

    // Firebase Analytics
    api(libs.firebase.analytics)

    // Firebase Authentication and Storage
    api(libs.firebase.auth.ktx)
    api(libs.firebase.config.ktx)

    // Firebase Crashlytics
    api(libs.firebase.crashlytics)
    api(libs.google.firebase.analytics)

    // GSON
    api(libs.gson)
}