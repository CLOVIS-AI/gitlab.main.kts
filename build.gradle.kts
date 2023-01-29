import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")

    id("maven-publish")
}

group = "opensavvy"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("gitlab-ci") {
            from(components["java"])
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    plugins.apply("maven-publish")

    publishing {
        repositories {
            val projectId = System.getenv("CI_PROJECT_ID")
            val token = System.getenv("CI_JOB_TOKEN")

            if (projectId != null && token != null)
                maven {
                    name = "GitLab"
                    url = uri("https://gitlab.com/api/v4/projects/$projectId/packages/maven")

                    credentials(HttpHeaderCredentials::class.java) {
                        name = "Job-Token"
                        value = token
                    }

                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                }
            else
                logger.debug("The GitLab registry is disabled because credentials are missing.")
        }
    }
}
