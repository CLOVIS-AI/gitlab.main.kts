/*
 * Copyright (c) 2026, OpenSavvy and contributors.
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

/**
 * Configuration for job failure tolerance.
 *
 * Use `allow_failure` to determine whether a pipeline should continue running when a job fails.
 *
 * ### Example
 *
 * To let the pipeline continue running subsequent jobs, use `allowFailure(true)`:
 * ```kotlin
 * val test by job {
 *     script {
 *         shell("exit 1")
 *     }
 *     allowFailure(true)
 * }
 * ```
 *
 * ### Example: exit codes
 *
 * You can also allow failure only for specific exit codes:
 * ```kotlin
 * val test by job {
 *     script {
 *         shell("exit 137")
 *     }
 *     allowFailure {
 *         onExitCode(137)
 *     }
 * }
 * ```
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#allow_failure)
 */
@GitLabCiDsl
class AllowFailure internal constructor() : YamlExport {

	private var enabled: Boolean? = null
	private var exitCodes: MutableSet<Int>? = null

	internal constructor(enabled: Boolean) : this() {
		this.enabled = enabled
	}

	/**
	 * Specifies which exit code is allowed to fail.
	 *
	 * If the job fails with this exit code, it will be considered successful (with a warning).
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * allowFailure {
	 *     onExitCode(137)
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#allow_failureexit_codes)
	 */
	@GitLabCiDsl
	fun onExitCode(code: Int) {
		if (exitCodes == null) {
			exitCodes = mutableSetOf()
		}
		exitCodes?.add(code)
	}

	override fun toYaml(): Yaml {
		val codes = exitCodes
		if (codes.isNullOrEmpty()) {
			return yaml(enabled ?: false)
		}

		return yamlMap {
			if (codes.size == 1) {
				add("exit_codes", yaml(codes.first().toLong()))
			} else {
				add("exit_codes", yamlList(codes.map { yaml(it.toLong()) }))
			}
		}
	}
}
