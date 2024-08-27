import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.android.detekt)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    jacoco
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.adhibuchori.kameraya"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adhibuchori.kameraya"
        minSdk = 26
        targetSdk = 34
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

    applicationVariants.all {
        val variantName = this.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // Define task names for unit tests and Android tests
        val unitTests = "test${variantName}UnitTest"
        val androidTests = "connected${variantName}AndroidTest"

        // Register a JacocoReport task for code coverage analysis
        tasks.register<JacocoReport>("Jacoco${variantName}CodeCoverage") {
            // Depend on unit tests and Android tests tasks
            dependsOn(listOf(unitTests, androidTests))
            // Set task grouping and description
            group = "Reporting"
            description = "Execute UI and unit tests, generate and combine Jacoco coverage report"
            // Configure reports to generate both XML and HTML formats
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
            // Set source directories to the main source directory
            sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))
            // Set class directories to compiled Java and Kotlin classes, excluding specified exclusions
            classDirectories.setFrom(files(
                fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
                    exclude(exclusions)
                },
                fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
                    exclude(exclusions)
                }
            ))
            // Collect execution data from .exec and .ec files generated during test execution
            executionData.setFrom(files(
                fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
            ))
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

val exclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*"
)

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.text.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Module
    implementation(project(":data"))
    implementation(project(":domain"))

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // Circular Image View
    implementation(libs.circleimageview)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // Data Store
    implementation(libs.androidx.datastore.preferences)

    // View Model and Live Data
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Koin
    implementation(libs.koin.android)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.core.testing)
    testImplementation("org.powermock:powermock-module-junit4:2.0.2")
}