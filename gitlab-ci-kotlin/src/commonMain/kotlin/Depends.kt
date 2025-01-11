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

import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlMap

data class Depends(
	val job: Job,
	val artifacts: Boolean,
	val optional: Boolean,
) : YamlExport {

	override fun toYaml() = yamlMap(
		yaml("job") to yaml(job.name),
		yaml("artifacts") to yaml(artifacts),
		yaml("optional") to yaml(optional)
	)

}
