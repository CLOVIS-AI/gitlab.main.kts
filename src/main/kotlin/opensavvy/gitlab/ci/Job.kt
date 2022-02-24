package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.script.Command
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.utils.generateReadOnlyDelegateProvider
import opensavvy.gitlab.ci.yaml.Yaml

class Job(
	var name: String,
) : YamlExport {

	var stage: Stage? = null

	internal var script = ArrayList<Command>()
	internal var beforeScript = ArrayList<Command>()
	internal var afterScript = ArrayList<Command>()
	internal var allowFailure: ConditionalFailure = ConditionalFailure.Always(false)
	internal var artifact = Artifact()

	override fun toYaml(): Yaml {
		val elements = HashMap<Yaml, Yaml>()

		if (stage != null)
			elements[Yaml.Scalar.StringLiteral("stage")] = Yaml.Scalar.StringLiteral(stage!!.name)

		elements[Yaml.Scalar.StringLiteral("allow_failure")] = allowFailure.toYaml()

		if (script.isNotEmpty())
			elements[Yaml.Scalar.StringLiteral("script")] = Yaml.Collection.ListLiteral(script.map { it.toYaml() })

		if (beforeScript.isNotEmpty())
			elements[Yaml.Scalar.StringLiteral("before_script")] =
				Yaml.Collection.ListLiteral(beforeScript.map { it.toYaml() })

		if (afterScript.isNotEmpty())
			elements[Yaml.Scalar.StringLiteral("after_script")] =
				Yaml.Collection.ListLiteral(afterScript.map { it.toYaml() })

		elements[Yaml.Scalar.StringLiteral("artifacts")] = artifact.toYaml()

		return Yaml.Collection.MapLiteral(elements)
	}
}

fun Job.script(block: CommandDsl.() -> Unit) = CommandDsl(script).block()
fun Job.beforeScript(block: CommandDsl.() -> Unit) = CommandDsl(beforeScript).block()
fun Job.afterScript(block: CommandDsl.() -> Unit) = CommandDsl(afterScript).block()

fun GitLabCi.job(name: String, block: Job.() -> Unit) = Job(name)
	.apply(block)
	.also { jobs += it }

fun GitLabCi.job(block: Job.() -> Unit = {}) = generateReadOnlyDelegateProvider { parent, property ->
	job(property.name, block)
}
