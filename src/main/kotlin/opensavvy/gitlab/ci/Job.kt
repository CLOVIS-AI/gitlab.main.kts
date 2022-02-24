package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.script.Command
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.utils.generateReadOnlyDelegateProvider
import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
import org.intellij.lang.annotations.Language

class Job(
	var name: String,
) : YamlExport {

	var stage: Stage? = null

	internal var script = ArrayList<Command>()
	internal var beforeScript = ArrayList<Command>()
	internal var afterScript = ArrayList<Command>()
	internal var allowFailure: ConditionalFailure = ConditionalFailure.Always(false)
	internal val artifact = Artifact()
	internal val cache = ArrayList<Cache>()
	internal var coverage: String? = null
	internal val dependencies = ArrayList<Job>()
	internal var needs: ArrayList<Job>? = null
	internal var image: ContainerImage? = null
	internal var services = ArrayList<ContainerService>()
	internal val tags = ArrayList<String>()

	override fun toYaml(): Yaml {
		val elements = HashMap<Yaml, Yaml>()

		if (stage != null)
			elements[yaml("stage")] = yaml(stage!!.name)

		elements[yaml("allow_failure")] = allowFailure.toYaml()

		if (script.isNotEmpty())
			elements[yaml("script")] = yaml(script.map { it.toYaml() })

		if (beforeScript.isNotEmpty())
			elements[yaml("before_script")] =
				yaml(beforeScript.map { it.toYaml() })

		if (afterScript.isNotEmpty())
			elements[yaml("after_script")] =
				yaml(afterScript.map { it.toYaml() })

		elements[yaml("artifacts")] = artifact.toYaml()

		if (cache.isNotEmpty())
			elements[yaml("cache")] = yaml(cache.map { it.toYaml() })

		if (dependencies.isNotEmpty())
			elements[yaml("dependencies")] = yaml(dependencies.map { yaml(it.name) })

		if (needs?.isNotEmpty() == true)
			elements[yaml("needs")] = yaml(needs!!.map { yaml(it.name) } + dependencies.map { yaml(it.name) })

		if (image != null)
			elements[yaml("image")] = image!!.toYaml()

		if (services.isNotEmpty())
			elements[yaml("services")] = yaml(services.map { it.toYaml() })

		if (tags.isNotEmpty())
			elements[yaml("tags")] = yaml(tags.map { yaml(it) })

		return yaml(elements)
	}
}

fun Job.script(block: CommandDsl.() -> Unit) = CommandDsl(script).block()
fun Job.beforeScript(block: CommandDsl.() -> Unit) = CommandDsl(beforeScript).block()
fun Job.afterScript(block: CommandDsl.() -> Unit) = CommandDsl(afterScript).block()
fun Job.coverage(@Language("RegExp") coveragePercentage: String) {
	coverage = coveragePercentage
}

fun Job.tag(name: String) {
	tags += name
}

fun Job.downloadArtifactsFrom(parentJob: Job) {
	dependencies += parentJob
}

fun Job.waitFor(previousJob: Job) {
	if (needs == null)
		needs = ArrayList<Job>().apply { this += previousJob }
	else
		needs!! += previousJob
}

fun GitLabCi.job(name: String, block: Job.() -> Unit) = Job(name)
	.apply(block)
	.also { jobs += it }

fun GitLabCi.job(block: Job.() -> Unit = {}) = generateReadOnlyDelegateProvider { parent, property ->
	job(property.name, block)
}
