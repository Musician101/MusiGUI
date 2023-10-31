dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = "common"
            version = "${project.version}"

            from(components["java"])
        }
    }
}
