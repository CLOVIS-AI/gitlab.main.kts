plugins {
    kotlin("jvm")

    id("com.palantir.git-version")
    id("com.adarshr.test-logger")
    id("maven-publish")
}

group = "opensavvy"
version = calculateVersion()

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    // Force the environment to ensure the test results are the same on all branches
    environment(
        "CI_COMMIT_BRANCH" to "main",
        "CI_DEFAULT_BRANCH" to "main",
        "CI_COMMIT_TAG" to "2.0",
    )
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

    if (System.getenv("CI") != null) {
        plugins.apply("com.adarshr.test-logger")

        testlogger {
            theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
        }
    }
}

fun calculateVersion(): String {
    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
    val details = versionDetails()

    return if (details.commitDistance == 0)
        details.lastTag
    else
        "${details.lastTag}-post.${details.commitDistance}+${details.gitHash}"
}
