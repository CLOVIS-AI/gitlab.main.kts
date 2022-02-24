package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.yaml

class GitLabCi : YamlExport {
	internal val stages = LinkedHashSet<Stage>()
	internal val jobs = ArrayList<Job>()

	override fun toYaml() = yaml(
		yaml("stages") to yaml(
			stages.toList().map { yaml(it.name) }
		),
		*(jobs.map { yaml(it.name) to it.toYaml() }.toTypedArray())
	)

	fun println() = println(toYaml().toYamlString())
}

fun gitlabCi(block: GitLabCi.() -> Unit) = GitLabCi().apply(block)
