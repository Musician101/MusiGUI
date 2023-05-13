repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":common"))
    api("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = "paper"
            version = "${project.version}"

            from(components["java"])
        }
    }
}
