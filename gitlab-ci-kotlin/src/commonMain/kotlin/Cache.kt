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
class Cache : YamlExport {
	private val includes = ArrayList<String>()
	private val keyFiles = ArrayList<String>()

	@GitLabCiDsl
	fun include(path: String) {
		includes += path
	}

	@GitLabCiDsl
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
