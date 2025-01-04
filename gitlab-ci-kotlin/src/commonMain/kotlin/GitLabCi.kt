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
import opensavvy.gitlab.ci.yaml.yamlList
import opensavvy.gitlab.ci.yaml.yamlMap

/**
 * Entrypoint to the GitLab CI pipeline generation.
 *
 * A pipeline is split into multiple [stages][Stage], each split into multiple [jobs][Job].
 *
 * To create a pipeline, use the factory function:
 * ```kotlin
 * val pipeline = gitlabCi {
 *     // Declare the different stages
 *     // Declare the different jobs
 * }
 * ```
 *
 * Once the pipeline is configured as you'd like, call [toYaml] or [println] to build the configuration file.
 */
class GitLabCi : YamlExport {
	internal val stages = LinkedHashSet<Stage>()
	internal val jobs = ArrayList<Job>()

	override fun toYaml() = yamlMap(
		yaml("stages") to yamlList(
			stages.toList().map { yaml(it.name) }
		),
		*(jobs.map { yaml(it.name) to it.toYaml() }.toTypedArray())
	)

	/**
	 * Generates the Yaml of this pipeline, and prints it to the standard output.
	 */
	fun println() = println(toYaml().toYamlString())
}

/**
 * Convenience factory function for [GitLabCi].
 */
fun gitlabCi(block: GitLabCi.() -> Unit) = GitLabCi().apply(block)
