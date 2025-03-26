plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.Lince"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.cdimascio:java-dotenv:5.2.2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.google.guava:guava:33.4.5-jre")
    implementation("com.google.code.gson:gson:2.12.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}