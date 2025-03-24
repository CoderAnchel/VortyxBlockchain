plugins {
    id("java")
}

group = "org.Lince"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.google.guava:guava:33.4.5-jre")
    implementation("com.google.code.gson:gson:2.12.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}