// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services)
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.android.library) apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    alias (libs.plugins.android.detekt) apply false
}