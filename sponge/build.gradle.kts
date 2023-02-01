plugins {
    id("org.spongepowered.gradle.plugin")
}

sponge {
    apiVersion("9.0.0")
}

dependencies {
    api(project(":common"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = "sponge"
            version = "${project.version}"

            from(components["java"])
        }
    }
}
