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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ContainerImage) return false

		if (name != other.name) return false
		if (entrypoint != other.entrypoint) return false

		return true
	}

	override fun hashCode(): Int {
		var result = name.hashCode()
		result = 31 * result + (entrypoint?.hashCode() ?: 0)
		return result
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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ContainerService) return false
		if (!super.equals(other)) return false

		if (alias != other.alias) return false
		if (command != other.command) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + (alias?.hashCode() ?: 0)
		result = 31 * result + (command?.hashCode() ?: 0)
		return result
	}
}

fun Job.image(name: String, block: ContainerImage.() -> Unit = {}) {
	image = ContainerImage(name).apply(block)
}

fun Job.service(name: String, block: ContainerImage.() -> Unit = {}) {
	services += ContainerService(name).apply(block)
}
