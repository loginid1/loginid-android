plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.loginid.auth"
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
}