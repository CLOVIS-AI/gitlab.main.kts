package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.yaml

/**
 * Entrypoint to the GitLab CI pipeline generation.
 *
 * A pipeline is split into multiple [stages][Stage], each split into multiple [jobs][Job].
 *
 * To create a pipeline, use the factory function:
 * ```kotlin
 * val pipeline = gitlabCi {
 *     // Declare the different stages
 *     // Declare the different jobs
 * }
 * ```
 *
 * Once the pipeline is configured as you'd like, call [toYaml] or [println] to build the configuration file.
 */
class GitLabCi : YamlExport {
	internal val stages = LinkedHashSet<Stage>()
	internal val jobs = ArrayList<Job>()

	override fun toYaml() = yaml(
		yaml("stages") to yaml(
			stages.toList().map { yaml(it.name) }
		),
		*(jobs.map { yaml(it.name) to it.toYaml() }.toTypedArray())
	)

	/**
	 * Generates the Yaml of this pipeline, and prints it to the standard output.
	 */
	fun println() = println(toYaml().toYamlString())
}

/**
 * Convenience factory function for [GitLabCi].
 */
fun gitlabCi(block: GitLabCi.() -> Unit) = GitLabCi().apply(block)
