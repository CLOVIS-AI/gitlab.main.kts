# Module GitLab CI Kotlin DSL

Generate your GitLab CI pipeline with Kotlin.

<a href="https://search.maven.org/search?q=opensavvy%20gitlab-ci-kotlin"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.gitlab/gitlab-ci-kotlin.svg?label=Maven%20Central"></a>
<a href="https://opensavvy.dev/open-source/stability.html"><img src="https://badgen.net/static/Stability/beta/purple"></a>
<a href="https://javadoc.io/doc/dev.opensavvy.gitlab/gitlab-ci-kotlin"><img src="https://badgen.net/static/Other%20versions/javadoc.io/blue"></a>

## Configuration

First, install [Kotlin](https://kotlinlang.org/). You can do so using your package manager of choice.
Create a `gitlab-ci.main.kts` file with the following content:

```kotlin
#!/usr/bin/env kotlin

@file:DependsOn("dev.opensavvy.gitlab:gitlab-ci-kotlin-jvm:VERSION-HERE") // See https://gitlab.com/opensavvy/automation/gitlab-ci.kt/-/releases

import opensavvy.gitlab.ci.*
import opensavvy.gitlab.ci.script.*

val pipeline = gitlabCi {
	val test by stage()

	val helloWorld by job(stage = test) {
		script {
			shell("echo 'Hello world'")
		}
	}
}

pipeline.println()
```

Allow execution for this file (on UNIX machines: `chmod u+x gitlab-ci.main.kts`), then execute it (on UNIX machines: `./gitlab-ci.main.kts`). Congratulation, you have just written your first GitLab CI pipeline in Kotlin!

# Package opensavvy.gitlab.ci

Jobs, stages, dependencies and more: declaring a GitLab CI pipeline from Kotlin.

Most of the objects are identical, or very similar, to their equivalent [in the Yaml format](https://docs.gitlab.com/ee/ci/yaml/).
Some have been renamed or restructured for readability.

## Overview

A pipeline is created by the [gitlabCi][opensavvy.gitlab.ci.gitlabCi] function.
It can be converted to Yaml by calling its [println][opensavvy.gitlab.ci.GitLabCi.println] function:

```kotlin
gitlabCi {
	// Declare your pipeline here
}.println()
```

A pipeline is composed of [stages][opensavvy.gitlab.ci.Stage], themselves composed of [jobs][opensavvy.gitlab.ci.Job].
Each job is a unit of work, which will be executed independently by GitLab.
A job can do anything that you can write a program for.
Most commonly, jobs use Docker images to provide a ready-to-use environment.

```kotlin
gitlabCi {
	// Create two stages: test and deploy
	val test by stage()
	val deploy by stage()

	// First job to test our app
	val build by job(stage = test) {
		image("ubuntu")

		script {
			shell("make")
		}

		artifacts {
			// The 'build' directory will be stored when the job is over
			include("build")
		}
	}

	if (Value.isDefaultBranch) {
		val publish by job(stage = deploy) {
			image("ubuntu")

			// Wait for 'build' to finish before starting,
			// and download the artifacts it generated
			dependsOn(build, artifacts = true)

			script {
				// Add your own logic
			}
		}
	}
}.println()
```

## Reading order

Start with [gitlabCi][opensavvy.gitlab.ci.gitlabCi], [stages][opensavvy.gitlab.ci.Stage] and [jobs][opensavvy.gitlab.ci.Job].

# Package opensavvy.gitlab.ci.plugins

Easily use Gradle, Helm, Docker and more within your pipelines.

## Usage

Plugins typically need to be installed on a [Job][opensavvy.gitlab.ci.Job] for their methods to work within that job. To do so, look for methods prefixed with `use` on the plugin's companion object.

For example, using the GitLab Docker Registry from within a job requires calling the method [useContainerRegistry][opensavvy.gitlab.ci.plugins.Docker.Companion.useContainerRegistry] in the job body.

# Package opensavvy.gitlab.ci.script

Methods related to declaring the `script` part of jobs.

See [shell][opensavvy.gitlab.ci.script.shell].

# Package opensavvy.gitlab.ci.utils

Helpers to make the syntax nicer.

# Package opensavvy.gitlab.ci.yaml

Lightweight embedded implementation of a Yaml serializer.
