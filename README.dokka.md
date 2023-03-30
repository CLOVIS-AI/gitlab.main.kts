# Module gitlab-ci.kt

Authoring complex GitLab CI builds is often made more complicated by the need to express them in Yaml.
Instead, this project aims to create GitLab CI pipelines from Kotlin.

Here are a few benefits of using a real programming language:

- Better autocompletion and IDE support,
- Code highlighting in embedded scripts using IntelliJ,
- Support for abstractions like conditions, loops, functions and higher-order functions,
- Built-in plugins to make configuration easier,
- Versioning: we (the maintainers) can deprecate and remove behavior without breaking your builds.

This library is open source. To contribute or report issues, see [our repository](https://gitlab.com/opensavvy/gitlab-ci.kt/).

## Usage

### Creating a script on your machine

First, install [Kotlin](https://kotlinlang.org/). You can do so using your package manager of choice.
Create a `gitlab-ci.main.kts` file with the following content:

```kotlin
#!/usr/bin/env kotlin

@file:Repository("https://gitlab.com/api/v4/projects/33995298/packages/maven")
@file:DependsOn("opensavvy:gitlab-ci.kt:VERSION-HERE") // See https://gitlab.com/opensavvy/gitlab-ci.kt/-/releases

import opensavvy.gitlab.ci.*
import opensavvy.gitlab.ci.script.*
import opensavvy.gitlab.ci.plugins.*

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

### Running a CI pipeline

> This section will be written in the feature. Please see [#8](https://gitlab.com/opensavvy/gitlab-ci.kt/-/issues/8).

## Plugins

In addition to the regular GitLab CI configuration, we also provide built-in plugins that can preconfigure common tools for you.
For example, using the [Docker plugin][opensavvy.gitlab.ci.plugins.Docker], it is easy to build a Docker image:

```kotlin
val image = "${Variable.Registry.image}/project"

gitlabCi {
	val buildImage by job {
		// Register the services required to use DinD
		// https://docs.gitlab.com/ee/ci/docker/using_docker_build.html#use-the-docker-executor-with-docker-in-docker
		useDockerInDocker()

		// Authenticates to the GitLab container registry for the project running the pipeline
		// https://docs.gitlab.com/ee/user/packages/container_registry/
		useContainerRegistry()

		script {
			docker.build(image, dockerfile = "project/Dockerfile")
			docker.push(image)
		}
	}
}
```

# Package opensavvy.gitlab.ci

# Package opensavvy.gitlab.ci.plugins

# Package opensavvy.gitlab.ci.script

# Package opensavvy.gitlab.ci.utils

# Package opensavvy.gitlab.ci.yaml
