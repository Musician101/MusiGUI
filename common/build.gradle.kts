dependencies {
    api("com.google.code.findbugs:jsr305:3.0.2")
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
