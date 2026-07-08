plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.loginid.core"
}

dependencies {
    implementation(project(":api"))
    implementation(libs.moshi)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.security.crypto)

    ksp(libs.moshi.kotlin.codegen)
}