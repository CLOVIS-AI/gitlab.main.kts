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
import opensavvy.gitlab.ci.yaml.yamlMap

/**
 * Selects the site profile and scanner profile used by a DAST job.
 *
 * Both profiles must already exist in the project. The job's stage must be `dast`.
 *
 * ### Example
 *
 * ```kotlin
 * val dast by job {
 *     dastConfiguration {
 *         siteProfile("Example Co")
 *         scannerProfile("Quick Passive Test")
 *     }
 * }
 * ```
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#dast_configuration)
 */
@GitLabCiDsl
class DastConfiguration : YamlExport {
	private var siteProfile: String? = null
	private var scannerProfile: String? = null

	/**
	 * Declares the site profile used by this job.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#dast_configuration)
	 */
	@GitLabCiDsl
	fun siteProfile(name: String) {
		this.siteProfile = name
	}

	/**
	 * Declares the scanner profile used by this job.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#dast_configuration)
	 */
	@GitLabCiDsl
	fun scannerProfile(name: String) {
		this.scannerProfile = name
	}

	override fun toYaml(): Yaml = yamlMap {
		addNotNull("site_profile", siteProfile)
		addNotNull("scanner_profile", scannerProfile)
	}
}
