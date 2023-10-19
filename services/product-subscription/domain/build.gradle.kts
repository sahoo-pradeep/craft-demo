plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("maven-publish")
}

dependencies {
    // internal libraries
    implementation("demo.craft:common-domain:0.0.2-SNAPSHOT")

    // external libraries
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0") // jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation(kotlin("stdlib-jdk8"))

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

publishing {
    publications {
        create<MavenPublication>("jar") {
            artifactId = "product-subscription-domain"
            from(components["java"])
        }
    }
}