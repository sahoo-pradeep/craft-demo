plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("maven-publish")
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // jdbcTemplate
    implementation("io.github.microutils:kotlin-logging:1.7.9") // logging
    implementation(kotlin("stdlib-jdk8"))

    // BOM imports
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.6.1"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("jar") {
            artifactId = "common-lock"
            from(components["java"])
        }
    }
}