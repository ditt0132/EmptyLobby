plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "dittonut"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:f09d3db999")
    implementation("com.google.guava:guava:33.2.1-jre")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    manifest {
        // Change this to your main class
        attributes["Main-Class"] = "dittonut.Main"
    }
}
