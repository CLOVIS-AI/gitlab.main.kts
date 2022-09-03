package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.script.Command
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.utils.generateReadOnlyDelegateProvider
import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml

/**
 * A single execution step in a [pipeline][GitLabCi].
 *
 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/jobs/).
 */
class Job(
	val name: String,
	val stage: Stage? = null,
) : YamlExport {

	internal var image: ContainerImage? = null
	internal var script = ArrayList<Command>()
	internal var beforeScript = ArrayList<Command>()
	internal var afterScript = ArrayList<Command>()
	internal val tags = HashSet<String>()

	override fun toYaml(): Yaml {
		val elements = HashMap<Yaml, Yaml>()

		if (image != null)
			elements[yaml("image")] = yaml(image!!.toYaml())

		if (stage != null)
			elements[yaml("stage")] = yaml(stage.name)

		if (script.isNotEmpty())
			elements[yaml("script")] = yaml(script.map { it.toYaml() })

		if (beforeScript.isNotEmpty())
			elements[yaml("before_script")] =
				yaml(beforeScript.map { it.toYaml() })

		if (afterScript.isNotEmpty())
			elements[yaml("after_script")] =
				yaml(afterScript.map { it.toYaml() })

		if (tags.isNotEmpty())
			elements[yaml("tags")] = yaml(tags.map { yaml(it) })

		return yaml(elements)
	}
}

/**
 * Adds commands to execute in this job.
 *
 * Example:
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
 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#script).
 */
fun Job.script(block: CommandDsl.() -> Unit) = CommandDsl(script).block()

/**
 * Adds commands to execute before the main script of this job.
 *
 * `beforeScript` has the same behavior as [script].
 *
 * Read more about the differences between [script] and `beforeScript` in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#before_script).
 */
fun Job.beforeScript(block: CommandDsl.() -> Unit) = CommandDsl(beforeScript).block()

/**
 * Adds commands to execute after the main script of this job.
 *
 * `afterScript` has the same behavior as [script].
 *
 * Read more about the differences between [script] and `afterScript` in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#after_script).
 */
fun Job.afterScript(block: CommandDsl.() -> Unit) = CommandDsl(afterScript).block()

/**
 * Adds a tag to this job.
 *
 * Only runners that are possess the same tag(s) will be able to execute this job.
 * For example, to run a job only on runners that run on ArchLinux and have Docker:
 * ```kotlin
 * val test by job {
 *    tag("archlinux")
 *    tag("docker")
 *
 *    script { … }
 * }
 * ```
 * The tags themselves do not have any special meaning.
 *
 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/yaml/#tags).
 */
fun Job.tag(name: String) {
	tags += name
}

fun GitLabCi.job(name: String, stage: Stage? = null, block: Job.() -> Unit) = Job(name, stage)
	.apply(block)
	.also { jobs += it }

fun GitLabCi.job(name: String? = null, stage: Stage? = null, block: Job.() -> Unit = {}) =
	generateReadOnlyDelegateProvider { parent, property ->
		job(name ?: property.name, stage, block)
	}
