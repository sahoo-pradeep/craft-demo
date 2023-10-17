plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // external dependencies
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0") // jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("io.github.microutils:kotlin-logging:1.7.9") // logging
    implementation(kotlin("stdlib-jdk8"))
	implementation("org.springframework.kafka:spring-kafka") // kafka
    implementation("io.micrometer:micrometer-registry-prometheus") // metrics and tracing

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
