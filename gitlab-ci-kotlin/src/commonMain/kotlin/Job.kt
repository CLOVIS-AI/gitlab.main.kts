/*
 * Copyright (c) 2022-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.script.Command
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.utils.generateReadOnlyDelegateProvider
import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlList
import opensavvy.gitlab.ci.yaml.yamlMap
import org.intellij.lang.annotations.Language

/**
 * A single execution step in a [pipeline][GitLabCi].
 *
 * Jobs are instantiated using the [job] factory. See its documentation for more information.
 */
@GitLabCiDsl
class Job internal constructor(
	/**
	 * The name of this job, as it appears in the GitLab UI.
	 */
	val name: String,
	/**
	 * The [Stage] this job is a part of.
	 *
	 * Stages are a simple way to group jobs and execution order.
	 * However, jobs can ignore this order, for example by using [dependsOn].
	 *
	 * If set to `null`, see the [official documentation](https://docs.gitlab.com/ee/ci/yaml/#stage).
	 */
	val stage: Stage? = null,
) : YamlExport {

	//region Image & services

	/**
	 * The container image used to execute this job, if any.
	 */
	private var image: ContainerImage? = null
		set(value) {
			if (field != null && field != value)
				System.err.println("Job '$name': setting the image to $value overrides previous setting $field")
			field = value
		}

	/**
	 * The container image used to execute this job.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val ubuntu by job {
	 *     image("ubuntu")
	 *
	 *     script {
	 *         shell("echo 'Hello world!'")
	 *     }
	 * }
	 * ```
	 *
	 * In this example, the runner will download the latest Ubuntu image and run the bash script that prints `Hello world!`.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#image).
	 */
	@GitLabCiDsl
	fun image(name: String, version: String = "latest", configuration: ContainerImage.() -> Unit = {}) {
		image = ContainerImage("$name:$version").apply(configuration)
	}

	private val services = HashSet<ContainerService>()

	/**
	 * Additional container images used by this job.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val docker by job {
	 *     image("docker")
	 *     service("docker", version = "dind")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#services).
	 */
	@GitLabCiDsl
	fun service(name: String, version: String = "latest", configuration: ContainerService.() -> Unit = {}) {
		services += ContainerService("$name:$version").apply(configuration)
	}

	//endregion
	//region Script

	private val script = ArrayList<Command>()

	/**
	 * Adds commands to execute in this job.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("echo Hello World")
	 *     }
	 *
	 *     script {
	 *         shell("echo You can call 'script' multiple times")
	 *         shell("echo or add multiple commands inside a single 'script' call")
	 *     }
	 * }
	 * ```
	 *
	 * Calling `script` multiple times executes the commands after the previous ones (the execution happens in the same order as in the source code).
	 *
	 * See [CommandDsl] to see what functions are available in `script`.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#script).
	 */
	@GitLabCiDsl
	fun script(block: CommandDsl.() -> Unit) {
		CommandDsl(script).block()
	}

	private val beforeScript = ArrayList<Command>()

	/**
	 * Adds commands to execute before the main script of this job.
	 *
	 * `beforeScript` has the same syntax as [script].
	 *
	 * The main usage of `beforeScript` is the ability to declare a script that will execute before all already-registered
	 * calls to [script].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     beforeScript {
	 *         shell("apt upgrade")
	 *     }
	 *
	 *     script {
	 *         shell("echo Hello world")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * Read more about the differences between [script] and `beforeScript` in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#before_script).
	 */
	@GitLabCiDsl
	fun beforeScript(block: CommandDsl.() -> Unit) {
		CommandDsl(beforeScript).block()
	}

	private val afterScript = ArrayList<Command>()

	/**
	 * Adds commands to execute after the main script of this job.
	 *
	 * `afterScript` has the same syntax as [script].
	 *
	 * The main usage of `afterScript` is the ability to declare a script that will execute after all [script] calls,
	 * even those that haven't happened yet.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("make")
	 *     }
	 *
	 *     afterScript {
	 *         // Something that executes after all scripts
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * Read more about the differences between [script] and `afterScript` in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#after_script).
	 */
	@GitLabCiDsl
	fun afterScript(block: CommandDsl.() -> Unit) {
		CommandDsl(afterScript).block()
	}

	//endregion
	//region Tags

	private val tags = HashSet<String>()

	/**
	 * Adds a tag to this job.
	 *
	 * Only runners that possess the same tag(s) will be able to execute this job.
	 *
	 * ### Example
	 *
	 * To run a job only on runners that are configured with the tags ArchLinux and Docker:
	 * ```kotlin
	 * val test by job {
	 *    tag("archlinux")
	 *    tag("docker")
	 *
	 *    script { … }
	 * }
	 * ```
	 *
	 * The tags themselves do not have any special meaning, though official GitLab Runners use a few standard ones.
	 * Each official runner is listed with its tags in the project settings' runner list.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#tags).
	 */
	@GitLabCiDsl
	fun tag(name: String) {
		tags += name
	}

	//endregion
	//region Dependencies

	private val needs = ArrayList<Depends>()

	/**
	 * This job will wait until [job] has terminated.
	 *
	 * By default, jobs start immediately.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * gitlabCi {
	 *     val compile by job {
	 *         script {
	 *             shell("make")
	 *         }
	 *
	 *         artifacts {
	 *             include("output")
	 *         }
	 *     }
	 *
	 *     val test by job {
	 *         dependsOn(compile, artifacts = true)
	 *
	 *         script {
	 *             shell("make test")
	 *         }
	 *     }
	 * }.println()
	 * ```
	 *
	 * ### Official documentation
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#needs)
	 *
	 * @param artifacts If set to `true`, the artifacts generated by [job] will be downloaded before the current job starts.
	 * @param optional If set to `true`, the job will run even if [job] was excluded from this pipeline (using `rules`, `only` or `when`).
	 */
	@GitLabCiDsl
	fun dependsOn(job: Job, artifacts: Boolean = false, optional: Boolean = false) {
		needs += Depends(job, artifacts, optional)
	}

	//endregion
	//region Variables

	private val variables = HashMap<String, String>()

	/**
	 * Declares an environment variable that will be available for the entire length of the job.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     variable("version", "1.0")
	 *
	 *     script {
	 *         shell("echo \$version")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#job-variables)
	 *
	 * @see Variable Access predefined CI/CD variables.
	 */
	@GitLabCiDsl
	fun variable(name: String, value: String) {
		variables[name] = value
	}

	//endregion
	//region Cache & artifacts

	private val cache = Cache()

	/**
	 * Declares paths that will be reused between different jobs, even if they are of different pipelines.
	 *
	 * Note that this is very easy to misuse in ways that make the pipeline less reliable or slower.
	 * In particular, avoid caching `node_modules` or `~/.gradle` or other dependency download caches if you're using
	 * GitLab Shared Runners, as downloading the cached dependencies can easily become slower than downloading the
	 * dependencies themselves.
	 *
	 * Instead, use systems like [Gradle's remote build cache](https://docs.gradle.org/current/userguide/build_cache.html),
	 * [GitLab's dependency proxy](https://docs.gitlab.com/ee/user/packages/dependency_proxy/).
	 *
	 * To learn more about configuring the cache, see [Cache].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     cache {
	 *         include(".gradle/wrapper")
	 *         keyFile("gradle/wrapper/gradle-wrapper.properties")
	 *     }
	 *
	 *     script {
	 *         echo("./gradlew test")
	 *     }
	 * }
	 * ```
	 *
	 * This particular example is inspired by the [Gradle plugin][opensavvy.gitlab.ci.plugins.Gradle], which we recommend
	 * instead of implementing this manually.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#cache)
	 */
	@GitLabCiDsl
	fun cache(configuration: Cache.() -> Unit) {
		cache.apply(configuration)
	}

	private val artifacts = Artifacts()

	/**
	 * Declares paths that will be saved at the end of the job, and can later be consumed by other jobs in the same
	 * pipeline.
	 *
	 * To learn more about configuring artifacts, see [Artifacts].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("echo '1.1' >> version.txt")
	 *     }
	 *
	 *     artifacts {
	 *         include("version.txt")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#artifacts)
	 */
	@GitLabCiDsl
	fun artifacts(configuration: Artifacts.() -> Unit) {
		artifacts.apply(configuration)
	}

	//endregion
	// region Code coverage

	private var coverageRegex: String? = null

	/**
	 * Declares a [regular expression][regex] to configure how code coverage is
	 * extracted from the job output. The coverage is shown in the UI if at least one
	 * line of the job output matches [regex].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("echo Code coverage: 99%")
	 *     }
	 *
	 *     coverage("Code coverage: \d+(?:\.\d+)?")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#coverage)
	 */
	@GitLabCiDsl
	fun coverage(@Language("JSRegexp") regex: String) {
		coverageRegex = "/$regex/"
	}

	// endregion

	override fun toYaml(): Yaml {
		val elements = HashMap<Yaml, Yaml>()

		if (image != null)
			elements[yaml("image")] = yaml(image!!)

		if (services.isNotEmpty())
			elements[yaml("services")] = yamlList(services)

		if (stage != null)
			elements[yaml("stage")] = yaml(stage.name)

		if (script.isNotEmpty())
			elements[yaml("script")] = yamlList(script)

		if (beforeScript.isNotEmpty())
			elements[yaml("before_script")] = yamlList(beforeScript)

		if (afterScript.isNotEmpty())
			elements[yaml("after_script")] = yamlList(afterScript)

		if (tags.isNotEmpty())
			elements[yaml("tags")] = yamlList(tags.map { yaml(it) })

		elements[yaml("needs")] = yamlList(needs)

		if (variables.isNotEmpty())
			elements[yaml("variables")] = yamlMap(variables)

		elements[yaml("cache")] = yaml(cache)

		elements[yaml("artifacts")] = yaml(artifacts)

		if (coverageRegex != null)
			elements[yaml("coverage")] = yaml(coverageRegex)

		return yamlMap(elements)
	}
}

/**
 * Declares a new [Job] in the current [pipeline][gitlabCi].
 *
 * A job is a unit of work in a pipeline: each job is executed by a runner.
 * Jobs can have [dependencies][Job.dependsOn], [artifacts][Job.artifacts] and more.
 *
 * ### Example
 *
 * ```kotlin
 * gitlabCi {
 *     val build by job {
 *         useGradle()
 *
 *         script {
 *             gradlew.task("build")
 *         }
 *     }
 * }.println()
 * ```
 *
 * ### External documentation
 *
 * - [What are jobs?](https://docs.gitlab.com/ee/ci/jobs)
 *
 * @param name The name of the job. See [Job.name].
 * If using the `by` syntax, the default name is the name of the variable the job is assigned to.
 * @param stage The [stage][Stage] this job is part of. If unset, see [the official documentation](https://docs.gitlab.com/ee/ci/yaml/#stage).
 */
@GitLabCiDsl
fun GitLabCi.job(name: String, stage: Stage? = null, block: Job.() -> Unit) = Job(name, stage)
	.apply(block)
	.also { jobs += it }

/**
 * Declares a new [Job] in the current [pipeline][gitlabCi].
 *
 * A job is a unit of work in a pipeline: each job is executed by a runner.
 * Jobs can have [dependencies][Job.dependsOn], [artifacts][Job.artifacts] and more.
 *
 * ### Example
 *
 * ```kotlin
 * gitlabCi {
 *     val build by job {
 *         useGradle()
 *
 *         script {
 *             gradlew.task("build")
 *         }
 *     }
 * }.println()
 * ```
 *
 * ### External documentation
 *
 * - [What are jobs?](https://docs.gitlab.com/ee/ci/jobs)
 *
 * @param name The name of the job. See [Job.name].
 * If using the `by` syntax, the default name is the name of the variable the job is assigned to.
 * @param stage The [stage][Stage] this job is part of. See [Job.stage].
 */
@GitLabCiDsl
fun GitLabCi.job(name: String? = null, stage: Stage? = null, block: Job.() -> Unit = {}) =
	generateReadOnlyDelegateProvider { parent, property ->
		parent.job(name ?: property.name, stage, block)
	}
