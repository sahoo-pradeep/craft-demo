plugins {
    id("org.springframework.boot")

    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    // internal modules
    implementation(project(":services:user-profile:api:server-stub"))
    implementation(project(":services:user-profile:common"))
    implementation(project(":services:user-profile:dao"))
    implementation(project(":services:user-profile:domain"))
    implementation(project(":services:user-profile:integration"))

    // internal libs
    implementation("demo.craft:common-communication:0.0.5-SNAPSHOT")
    implementation("demo.craft:common-domain:0.0.5-SNAPSHOT")
    implementation("demo.craft:common-lock:0.0.5-SNAPSHOT")
    implementation("demo.craft:common-cache:0.0.5-SNAPSHOT")
    implementation("demo.craft:product-subscription-client:0.0.5-SNAPSHOT")

    // external libs
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql") // postgres database
    implementation("org.flywaydb:flyway-core:6.5.2") // db migration
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0") // jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("io.github.microutils:kotlin-logging:1.7.9") // logging
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.kafka:spring-kafka")
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.openfeign:feign-okhttp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus") // metrics and tracing

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
    }
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.assertj:assertj-core:3.24.2")

    // BOM imports
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.6.1"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.0"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
