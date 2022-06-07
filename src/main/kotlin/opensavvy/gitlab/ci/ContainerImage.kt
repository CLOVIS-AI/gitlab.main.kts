package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml

open class ContainerImage(val name: String) : YamlExport {
	var entrypoint: List<String>? = null

	override fun toYaml(): Yaml.Collection.MapLiteral {
		val elements = HashMap<Yaml, Yaml>()

		elements[yaml("name")] = yaml(name)

		if (entrypoint != null)
			elements[yaml("entrypoint")] = yaml(entrypoint!!.map { yaml(it) })

		return yaml(elements)
	}
}

class ContainerService(name: String) : ContainerImage(name) {
	var alias: String? = null
	var command: List<String>? = null

	override fun toYaml(): Yaml.Collection.MapLiteral {
		val elements = super.toYaml().contents as MutableMap

		if (alias != null)
			elements[yaml("alias")] = yaml(alias!!)

		if (command?.isNotEmpty() == true)
			elements[yaml("command")] = yaml(command!!.map { yaml(it) })

		return yaml(elements)
	}
}

fun Job.image(name: String, block: ContainerImage.() -> Unit = {}) {
	image = ContainerImage(name).apply(block)
}

fun Job.service(name: String, block: ContainerImage.() -> Unit = {}) {
	services += ContainerService(name).apply(block)
}
