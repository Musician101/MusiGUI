plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
    }

    group = "io.musician101.musigui"
    version = "1.1.0"

    repositories {
        mavenCentral()
    }
}

