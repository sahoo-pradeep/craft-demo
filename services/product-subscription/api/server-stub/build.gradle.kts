import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("maven-publish")
}

dependencies {
    // external dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // external test dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    // BOM imports
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.16"))
}

val serverPath = "$rootDir/services/product-subscription/api/server-stub"

val preGenerateCleanup by tasks.registering(Delete::class) {
    delete = setOf(
        fileTree(serverPath) {
            setExcludes(listOf("build.gradle.kts", "build")) // Gradle's cache, helps in faster re-compiles
        })
    doLast {
        println("Cleaned server-stub dir")
    }
}

val generateServerStub by tasks.registering(GenerateTask::class) {
    dependsOn(preGenerateCleanup)

    inputSpec.set("$rootDir/services/product-subscription/api/spec.yml") // /cardboard/api/spec.yml
    outputDir.set(serverPath)

    // config
    generatorName.set("kotlin-spring")
    apiPackage.set("demo.craft.product.subscription.api")
    modelPackage.set("demo.craft.product.subscription.model")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "useTags" to "true",  // Use tags for the naming
            "interfaceOnly" to "true",   // Generating the Controller API interface and the models only
            "gradleBuildFile" to "false",
            "exceptionHandler" to "false",
            "enumPropertyNaming" to "UPPERCASE" // Generate enums in uppercase. 'x-enum-varnames' is no more required in api specs.
        ),
    )
}

val postGenerateCleanup by tasks.registering(Delete::class) {
    dependsOn(generateServerStub)

    delete = setOf(
        fileTree(serverPath) {
            setExcludes(listOf("build.gradle.kts", "build", "src")) // Gradle's cache, helps in faster re-compiles
        },
        fileTree("$serverPath/src/main/kotlin/org") // delete Application
    )
    doLast {
        println("Cleaned unnecessary files post generation")
    }
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(postGenerateCleanup)
}
