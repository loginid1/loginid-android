plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.loginid.auth"
}

dependencies {
    implementation(project(":api"))
    api(project(":core"))
}

mavenPublishing {
    coordinates("io.loginid", "auth", project.version.toString())

    pom {
        name.set("LoginID Auth")
        description.set("An authentication SDK specializing in passkey authentication.")
    }
}
