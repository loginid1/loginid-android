import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.gradle.kotlin.dsl.configure

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    group = "io.loginid"
    version = providers.gradleProperty("sdkVersion")
        .orElse("0.0.0-SNAPSHOT")
        .get()
}

subprojects {
    plugins.withId("com.android.library") {
        extensions.configure<LibraryExtension> {
            compileSdk {
                version = release(36) {
                    minorApiLevel = 1
                }
            }

            defaultConfig {
                minSdk = 28
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            lint {
                abortOnError = true
            }
        }

        dependencies {
            add("implementation", libs.androidx.appcompat)
            add("implementation", libs.androidx.core.ktx)

            add("testImplementation", libs.junit)

            add("androidTestImplementation", libs.androidx.junit)
            add("androidTestImplementation", libs.androidx.espresso.core)
        }
    }

    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<KotlinAndroidProjectExtension> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }

    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure(com.vanniktech.maven.publish.MavenPublishBaseExtension::class) {
            publishToMavenCentral()
            signAllPublications()

            pom {
                url.set("https://github.com/loginid1/loginid-android")

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("loginid")
                        name.set("LoginID")
                        email.set("support@loginid.io")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/loginid1/loginid-android.git")
                    developerConnection.set("scm:git:ssh://git@github.com:loginid1/loginid-android.git")
                    url.set("https://github.com/loginid1/loginid-android")
                }
            }
        }
    }
}

tasks.register("buildLibrariesRelease") {
    dependsOn(
        ":api:jar",
        ":core:assembleRelease",
        ":auth:assembleRelease",
        ":mfa:assembleRelease"
    )
}

tasks.register("buildSdkRelease") {
    val version = providers.gradleProperty("sdkVersion").orNull

    if (version != null) {
        println("Building SDK version $version")
    }

    dependsOn(
        ":api:jar",
        ":core:assembleRelease",
        ":auth:assembleRelease",
        ":mfa:assembleRelease"
    )
}
