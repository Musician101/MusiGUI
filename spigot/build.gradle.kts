repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/") {
        name = "Spigot"
    }
}

dependencies {
    api(project(":common"))
    api("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = "spigot"
            version = "${project.version}"

            from(components["java"])
        }
    }
}
