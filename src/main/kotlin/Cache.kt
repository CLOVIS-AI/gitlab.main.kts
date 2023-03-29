package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlList
import opensavvy.gitlab.ci.yaml.yamlMap

class Cache : YamlExport {
	private val includes = ArrayList<String>()
	private val keyFiles = ArrayList<String>()

	fun include(path: String) {
		includes += path
	}

	fun keyFile(path: String) {
		keyFiles += path
	}

	override fun toYaml(): Yaml {
		val data = HashMap<Yaml, Yaml>()

		data[yaml("paths")] = yamlList(includes)

		if (keyFiles.isNotEmpty())
			data[yaml("key")] = yamlMap(
				yaml("files") to yamlList(keyFiles)
			)

		return yamlMap(data)
	}
}
