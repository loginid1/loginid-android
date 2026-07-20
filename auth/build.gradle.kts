import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

android {
    namespace = "io.loginid.auth"

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.loginid"
            artifactId = "auth"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}