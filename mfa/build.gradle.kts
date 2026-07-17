import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    `maven-publish`
}

android {
    namespace = "com.loginid.mfa"

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))

    implementation(libs.moshi)

    ksp(libs.moshi.kotlin.codegen)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.loginid"
            artifactId = "mfa"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}