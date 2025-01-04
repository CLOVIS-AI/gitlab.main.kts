# GitLab CI Kotlin DSL

> This project is currently experimental.
> The documentation presents our objective, not necessarily the current state.
> Please report any issues you encounter [here](https://gitlab.com/opensavvy/automation/gitlab-ci.kt/-/issues/new).

[GitLab CI](https://docs.gitlab.com/ee/ci/) configuration is written in Yaml, in a [.gitlab-ci.yml](https://docs.gitlab.com/ee/ci/yaml/gitlab_ci_yaml.html) file. As projects grow, Yaml becomes unwieldy and reuse between jobs becomes harder. Since the introduction of CI, GitLab has provided many tools to reduce duplication: [global configuration](https://docs.gitlab.com/ee/ci/yaml/#default), [include](https://docs.gitlab.com/ee/ci/yaml/#include), [anchors](https://docs.gitlab.com/ee/ci/yaml/yaml_optimization.html#anchors), [extends](https://docs.gitlab.com/ee/ci/yaml/#extends) and [!reference](https://docs.gitlab.com/ee/ci/yaml/yaml_optimization.html#reference-tags). All of these still fall short of a real programming or scripting language, however, while adding yet another syntax that must be learned by users.

Kotlin is a modern statically-typed programming language created by JetBrains. Among its various uses, it's well suited for the creation of domain-specific languages (DSLs). Using Kotlin and this library, we can rewrite the following CI configuration easily:

```yaml
stages:
  - test

helloWorld:
  stage: test
  script:
    - echo "Hello world"
```

We create a `.gitlab-ci.main.kts` file, make it executable, and set its contents:

```kotlin
#!/usr/bin/env kotlin

@file:Repository("https://gitlab.com/api/v4/projects/33995298/packages/maven")
@file:DependsOn("dev.opensavvy.gitlab:gitlab-ci-kotlin:VERSION-HERE") // See https://gitlab.com/opensavvy/automation/gitlab-ci.kt/-/releases

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

The Yaml syntax shines in this small example, but the real world is much more complicated, and Kotlin provides much-needed features. For example:

- Included scripts and commands (e.g. `"echo 'Hello world''"` in this example) are properly color-highlighted by IntelliJ.
- You can use any programming abstraction for code reuse: `if`, `for`, functions…
- Type-safety with powerful auto-completion.
- API versioning: we can deprecate and remove syntax without breaking your build, upgrade when you're ready to.

## Ki, the Kotlin Interactive Shell

To try the project in [Ki](https://blog.jetbrains.com/kotlin/2021/04/ki-the-next-interactive-shell-for-kotlin/), run:

```text
[1] :repository https://gitlab.com/api/v4/projects/33995298/packages/maven
[2] :dependsOn opensavvy:gitlab-ci.kt:VERSION-HERE
[3] import opensavvy.gitlab.ci.*
[4] import opensavvy.gitlab.ci.script.*
[5] import opensavvy.gitlab.ci.plugins.*
```

## License

The project is licensed under Apache 2.0. [See the full text here](LICENSE.txt).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
- To learn more about our coding conventions and workflow, see the [OpenSavvy website](https://opensavvy.dev/open-source/index.html).
- This project is based on the [OpenSavvy Playground](docs/playground/README.md), a collection of preconfigured project templates.

If you don't want to clone this project on your machine, it is also available using [GitPod](https://www.gitpod.io/) and [DevContainer](https://containers.dev/) ([VS Code](https://code.visualstudio.com/docs/devcontainers/containers) • [IntelliJ & JetBrains IDEs](https://www.jetbrains.com/help/idea/connect-to-devcontainer.html)). Don't hesitate to create issues if you have problems getting the project up and running.
