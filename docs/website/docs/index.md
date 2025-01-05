---
template: home.html
---

# Welcome!

Authoring complex GitLab CI builds is often made more complicated by the need to express them in Yaml.
Instead, this project aims to create GitLab CI pipelines from [Kotlin](https://kotlinlang.org/).

By using an actual programming language, we gain:

- Better autocompletion and IDE support,
- Code highlighting in embedded scripts using IntelliJ,
- Support for abstractions like conditions, loops, functions and higher-order functions,
- Built-in [plugins](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci.plugins/index.md) to make configuration easier,
- Versioning: we (the maintainers) can deprecate and remove behavior without breaking your builds.

## Why?

[GitLab CI](https://docs.gitlab.com/ee/ci/) configuration is written in Yaml, in a [.gitlab-ci.yml](https://docs.gitlab.com/ee/ci/yaml/gitlab_ci_yaml.html) file. As projects grow, Yaml becomes unwieldy and reuse between jobs becomes harder. Since the introduction of CI, GitLab has provided many tools to reduce duplication: [global configuration](https://docs.gitlab.com/ee/ci/yaml/#default), [include](https://docs.gitlab.com/ee/ci/yaml/#include), [anchors](https://docs.gitlab.com/ee/ci/yaml/yaml_optimization.html#anchors), [extends](https://docs.gitlab.com/ee/ci/yaml/#extends) and [!reference](https://docs.gitlab.com/ee/ci/yaml/yaml_optimization.html#reference-tags). All of these still fall short of a real programming or scripting language, each adding yet another syntax that must be learned by users.

Kotlin is a modern statically-typed programming language created by JetBrains. Among its various uses, it's well suited for the creation of domain-specific languages (DSLs). Thanks for Kotlin's DSL powers, we can easily create complex pipelines:

```kotlin
gitlabCi {
	val test by stage() //(1)!
	
	val helloWorld by job(stage = test) { //(2)!
		script {
			shell("echo 'Hello world'")
		}
	}

	val k8sImage = "${Variable.Registry.image}/build/k8s" //(4)!

	val buildK8s = job("k8s:docker:build", stage = test) {
		useDockerInDocker() //(3)!
		useContainerRegistry()

		script {
			docker.build(k8sImage, dockerfile = "k8s.dockerfile")
			docker.push(k8sImage)
		}
	}

	if (Value.isDefaultBranch) { //(5)!
		job("deploy", stage = test) {
			dependsOn(buildK8s)

			image(k8sImage)

			beforeScript {
				shell("kubectl config use-context opensavvy/config:opensavvy-agent")
				helm.addRepository("itzg", "https://itzg.github.io/minecraft-server-charts")
				helm.updateRepositories()
			}

			script {
				helm.deploy(
					release = "minecraft",
					chart = "itzg/minecraft",
					values = listOf("minecraft.yml"),
					namespace = "minecraft"
				)
			}
		}
	}
}.println()
```

1. Declare a CI stage. [Learn more](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci/stage.md).
2. Declare a new job in that stage. [Learn more](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci/job.md).
3. Use plugins to easily configure Docker, Gradle, Helm and more. [Learn more](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci.plugins/index.md).
4. Easily and type-safely use the CI environment to conditionally create the pipeline. [Learn more](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci/-value).
5. Use regular `if`, `for`, `while` constructs or any other Kotlin functions—no need to learn about `extends`, `!reference` or anchors.

## Where do I start?

- [**Setting up a project**](tutorials/index.md)
- [**Start authoring pipelines**](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci/index.md)
- [**Learn more about plugins**](api/-git-lab%20-c-i%20-kotlin%20-d-s-l/opensavvy.gitlab.ci.plugins/index.md)

Don't hesitate to [star](https://gitlab.com/opensavvy/automation/gitlab-ci.kt) and share the project ❤️
