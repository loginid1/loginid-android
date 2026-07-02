plugins {
    kotlin("jvm")
    id("org.openapi.generator") version "7.23.0"
    id("com.google.devtools.ksp") version "2.3.9"
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

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("$generatedSourcesDir/src/main/kotlin")
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/openapi-gen/mod-openapi.yaml")
    outputDir.set(generatedSourcesDir)

    apiPackage.set("com.loginid.client.api")
    modelPackage.set("com.loginid.client.model")

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