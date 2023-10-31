repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":common"))
    compileOnly("org.jetbrains:annotations:24.0.1")
    api("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
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
