repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":common"))
    compileOnly("org.jetbrains:annotations:24.1.0")
    api("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
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
