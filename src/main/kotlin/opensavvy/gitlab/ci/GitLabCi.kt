package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml

class GitLabCi : YamlExport {
	internal val stages = LinkedHashSet<Stage>()
	internal val jobs = ArrayList<Job>()

	override fun toYaml() = Yaml.Collection.MapLiteral(
		Yaml.Scalar.StringLiteral("stages") to Yaml.Collection.ListLiteral(
			stages.toList().map { Yaml.Scalar.StringLiteral(it.name) }
		),
		*(jobs.map { Yaml.Scalar.StringLiteral(it.name) to it.toYaml() }.toTypedArray())
	)

	fun println() = println(toYaml().toYamlString())
}

fun gitlabCi(block: GitLabCi.() -> Unit) = GitLabCi().apply(block)
