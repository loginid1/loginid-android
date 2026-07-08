import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("org.openapi.generator") version "7.23.0"
    alias(libs.plugins.ksp)
}

val generatedSourcesDir = "${buildDir}/generated/openapi"

dependencies {
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)

    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

sourceSets.main {
    java.srcDir(files(generatedSourcesDir).builtBy(tasks.named("openApiGenerate")))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("openApiGenerate")
}

tasks.named("openApiGenerate") {
    mustRunAfter(tasks.named("clean"))
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/openapi-gen/mod-openapi.yaml")
    outputDir.set(generatedSourcesDir)

    apiPackage.set("com.loginid.client.api")
    modelPackage.set("com.loginid.client.model")
    cleanupOutput = true
    generateApiTests = false
    generateModelTests = false

    configOptions.set(
        mapOf(
            "apiSuffix" to "Api",
            "useCoroutines" to "true",
            "moshiCodeGen" to "true",
            "dateLibrary" to "string",
            "enumPropertyNaming" to "UPPERCASE",
            "jvmOverloads" to "true"
        )
    )
}