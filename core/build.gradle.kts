import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    `maven-publish`
}

android {
    namespace = "io.loginid.core"

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(project(":api"))
    implementation(libs.moshi)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.security.crypto)

    ksp(libs.moshi.kotlin.codegen)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.loginid"
            artifactId = "core"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}