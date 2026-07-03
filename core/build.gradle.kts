plugins {
    alias(libs.plugins.android.library)
    id("com.google.devtools.ksp") version "2.3.9"
}

android {
    namespace = "com.loginid.core"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":api"))
    implementation(libs.moshi)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}