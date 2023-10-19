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
    implementation("io.swagger:swagger-annotations:1.5.18")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.openapitools:jackson-databind-nullable:0.1.0")
    implementation("org.hibernate.validator:hibernate-validator:6.0.21.Final")

    // external test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.6.1")

    // BOM imports
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.6.1"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.0"))
}

publishing {
    publications {
        create<MavenPublication>("jar") {
            artifactId = "user-profile-client"
            from(components["java"])
        }
    }
}

val clientPath = "$rootDir/services/user-profile/api/client"

val preGenerateCleanup by tasks.registering(Delete::class) {
    delete = setOf(
        fileTree(clientPath) {
            setExcludes(listOf("build.gradle.kts", "build")) // Gradle's cache, helps in faster re-compiles
        })
    doLast {
        println("Cleaned client dir")
    }
}

val generateClient by tasks.registering(GenerateTask::class) {
    dependsOn(preGenerateCleanup)

    inputSpec.set("$rootDir/services/user-profile/api/spec.yml")
    outputDir.set(clientPath)

    // config
    generatorName.set("spring")
    library.set("spring-cloud")
    apiPackage.set("demo.craft.user.profile.client.api")
    modelPackage.set("demo.craft.user.profile.client.model")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "useTags" to "true",  // Use tags for the naming
            "interfaceOnly" to "true",   // Generating the Controller API interface and the models only
            "gradleBuildFile" to "false",
            "exceptionHandler" to "false",
            "enumPropertyNaming" to "UPPERCASE" // Generate enums in uppercase. 'x-enum-varnames' is no more required in api specs.
        )
    )
}

val postGenerateCleanup by tasks.registering(Delete::class) {
    dependsOn(generateClient)

    delete = setOf(
        fileTree(clientPath) {
            setExcludes(listOf("build.gradle.kts", "build", "src")) // Gradle's cache, helps in faster re-compiles
        }
    )
    doLast {
        println("Cleaned unnecessary files post generation")
    }
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(postGenerateCleanup)
}
