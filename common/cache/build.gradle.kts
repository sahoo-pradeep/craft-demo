plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("maven-publish")
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("io.github.microutils:kotlin-logging:1.7.9") // logging
    implementation(kotlin("stdlib-jdk8"))
    implementation("redis.clients:jedis:4.3.2") // redis cache

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
            artifactId = "common-cache"
            from(components["java"])
        }
    }
}