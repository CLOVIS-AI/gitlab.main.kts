/*
 * Copyright (c) 2022-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlList
import opensavvy.gitlab.ci.yaml.yamlMap

@GitLabCiDsl
open class ContainerImage internal constructor(val nameAndVersion: String) : YamlExport {
	var entrypoint: List<String>? = null

	override fun toYaml(): Yaml.Collection.MapLiteral {
		val elements = HashMap<Yaml, Yaml>()

		elements[yaml("name")] = yaml(nameAndVersion)

		if (entrypoint != null)
			elements[yaml("entrypoint")] = yamlList(entrypoint!!.map { yaml(it) })

		return yamlMap(elements)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ContainerImage) return false

		if (nameAndVersion != other.nameAndVersion) return false
		return entrypoint == other.entrypoint
	}

	override fun hashCode(): Int {
		var result = nameAndVersion.hashCode()
		result = 31 * result + (entrypoint?.hashCode() ?: 0)
		return result
	}
}

@GitLabCiDsl
class ContainerService internal constructor(name: String) : ContainerImage(name) {
	var alias: String? = null
	var command: List<String>? = null

	override fun toYaml(): Yaml.Collection.MapLiteral {
		val elements = super.toYaml().contents as MutableMap

		if (alias != null)
			elements[yaml("alias")] = yaml(alias!!)

		if (command?.isNotEmpty() == true)
			elements[yaml("command")] = yamlList(command!!.map { yaml(it) })

		return yamlMap(elements)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ContainerService) return false
		if (!super.equals(other)) return false

		if (alias != other.alias) return false
		return command == other.command
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + (alias?.hashCode() ?: 0)
		result = 31 * result + (command?.hashCode() ?: 0)
		return result
	}
}
