plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.loginid.mfa"
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))

    implementation(libs.moshi)

    ksp(libs.moshi.kotlin.codegen)
}