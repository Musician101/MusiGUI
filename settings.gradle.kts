rootProject.name = "MusiGUI"
pluginManagement {
    repositories {
        maven {
            name = "sponge"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
    }

    plugins {
        `java-library`
        java
        `maven-publish`
        id("org.spongepowered.gradle.plugin") version "1.1.1" apply false
    }
}

include("common", "paper", "spigot", "sponge")
