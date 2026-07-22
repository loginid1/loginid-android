plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.loginid.mfa"
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))

    implementation(libs.moshi)

    ksp(libs.moshi.kotlin.codegen)
}

mavenPublishing {
    coordinates("io.loginid", "mfa", project.version.toString())

    pom {
        name.set("LoginID MFA")
        description.set("An SDK for orchestrating multi-factor authentication (MFA) flows, supporting passkeys, OTP, and third party authentication.")
    }
}
