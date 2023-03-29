package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.utils.generateReadOnlyDelegateProvider

/**
 * A stage in a pipeline.
 *
 * A stage is a group of jobs that run together.
 * Use the [stage] factory to easily create a stage in a [pipeline][GitLabCi]:
 * ```kotlin
 * val pipeline = gitlabCi {
 *     val build by stage()
 *
 *     val start by job {
 *         stage = build
 *         script { … }
 *     }
 * }
 * ```
 *
 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#stages).
 */
data class Stage(
	val name: String,
)

/**
 * Creates a stage with a given [name].
 *
 * Example:
 * ```kotlin
 * val pipeline = gitlabCi {
 *     val build = stage("some-other-name")
 * }
 * ```
 *
 * To automatically generate the name from the variable, see [stage].
 */
fun GitLabCi.stage(name: String): Stage = Stage(name)
	.also { stages += it }

/**
 * Creates a stage automatically named after the variable it is assigned to.
 *
 * Example:
 * ```kotlin
 * val pipeline = gitlabCi {
 *     val build by stage()
 * }
 * ```
 *
 * To use a different name than the variable's name, use the [stage] function.
 */
fun GitLabCi.stage(name: String? = null) = generateReadOnlyDelegateProvider { pipeline, property ->
	pipeline.stage(name ?: property.name)
}
