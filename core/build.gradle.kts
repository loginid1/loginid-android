plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.loginid.core"
}

dependencies {
    implementation(project(":api"))
    implementation(libs.moshi)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.security.crypto)

    ksp(libs.moshi.kotlin.codegen)
}

mavenPublishing {
    coordinates("io.loginid", "core", project.version.toString())

    pom {
        name.set("LoginID Core")
        description.set("Shared internal code used across all LoginID Android modules")
    }
}
