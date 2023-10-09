plugins {
    id("org.springframework.boot")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql") // postgres database
    implementation("org.flywaydb:flyway-core:6.5.2") // db migration
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0") // jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("io.github.microutils:kotlin-logging:1.7.9") // logging
    implementation("org.jetbrains.kotlin:kotlin-reflect")
//	implementation("org.springframework.kafka:spring-kafka")
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.openfeign:feign-okhttp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.micrometer:micrometer-registry-prometheus") // metrics and tracing

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // BOM imports
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.16"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.0"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}