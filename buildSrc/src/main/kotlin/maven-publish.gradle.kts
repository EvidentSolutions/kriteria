package buildsrc.convention

plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.vanniktech.maven.publish")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaJavadoc"))
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "fi.evident.kriteria",
        artifactId = "kriteria-${project.name}",
        version = project.findProperty("projectVersion") as String? ?: "0.1.0-SNAPSHOT"
    )

    pom {
        name = provider { "kriteria-${project.name}" }
        description = provider { project.description }
        url = "https://github.com/EvidentSolutions/kriteria"
        inceptionYear = "2025"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "komu"
                name = "Juha Komulainen"
                url = "https://github.com/komu"
            }
        }

        scm {
            url = "https://github.com/EvidentSolutions/kriteria"
            connection = "scm:git:https://github.com/EvidentSolutions/kriteria.git"
            developerConnection = "scm:git:git@github.com:EvidentSolutions/kriteria.git"
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/EvidentSolutions/kriteria/issues"
        }
    }
}
