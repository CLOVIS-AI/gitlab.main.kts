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
class Artifacts : YamlExport {
	private val includes = ArrayList<String>()
	private val excludes = ArrayList<String>()

	private var expireIn: String? = null
	private var exposeAs: String? = null
	private var name: String? = null

	private val reports = HashMap<Yaml, Yaml>()

	private var rule = When.Always

	@GitLabCiDsl
	fun include(path: String) {
		includes += path
	}

	@GitLabCiDsl
	fun exclude(path: String) {
		excludes += path
	}

	@GitLabCiDsl
	fun expireIn(time: String) {
		expireIn = time
	}

	@GitLabCiDsl
	fun exposeAs(name: String) {
		exposeAs = name
	}

	@GitLabCiDsl
	fun name(name: String) {
		this.name = name
	}

	@GitLabCiDsl
	fun rule(rule: When) {
		this.rule = rule
	}

	//region Reports

	@GitLabCiDsl
	fun accessibility(path: String) {
		reports[yaml("accessibility")] = yaml(path)
	}

	@GitLabCiDsl
	fun apiFuzzing(path: String) {
		reports[yaml("api_fuzzing")] = yaml(path)
	}

	@GitLabCiDsl
	fun browserPerformance(path: String) {
		reports[yaml("browser_performance")] = yaml(path)
	}

	@GitLabCiDsl
	fun clusterImageScanning(path: String) {
		reports[yaml("cluster_image_scanning")] = yaml(path)
	}

	@GitLabCiDsl
	fun coverage(format: String, path: String) {
		reports[yaml("coverage_report")] = yamlMap(
			mapOf(
				"coverage_format" to format,
				"path" to path,
			)
		)
	}

	@GitLabCiDsl
	fun codeQuality(path: String) {
		reports[yaml("codequality")] = yaml(path)
	}

	@GitLabCiDsl
	fun containerScanning(path: String) {
		reports[yaml("container_scanning")] = yaml(path)
	}

	@GitLabCiDsl
	fun coverageFuzzing(path: String) {
		reports[yaml("coverage_fuzzing")] = yaml(path)
	}

	@GitLabCiDsl
	fun cyclonedx(vararg path: String) {
		reports[yaml("cyclonedx")] = yamlList(path.asList())
	}

	@GitLabCiDsl
	fun dast(path: String) {
		reports[yaml("dast")] = yaml(path)
	}

	@GitLabCiDsl
	fun dependencyScanning(path: String) {
		reports[yaml("dependency_scanning")] = yaml(path)
	}

	@GitLabCiDsl
	fun dotenv(path: String) {
		reports[yaml("dotenv")] = yaml(path)
	}

	@GitLabCiDsl
	fun junit(path: String) {
		reports[yaml("junit")] = yaml(path)
	}

	@GitLabCiDsl
	fun licenseScanning(path: String) {
		reports[yaml("license_scanning")] = yaml(path)
	}

	@GitLabCiDsl
	fun loadPerformance(path: String) {
		reports[yaml("load_performance")] = yaml(path)
	}

	@GitLabCiDsl
	fun metrics(path: String) {
		reports[yaml("metric")] = yaml(path)
	}

	@GitLabCiDsl
	fun requirements(path: String) {
		reports[yaml("requirements")] = yaml(path)
	}

	@GitLabCiDsl
	fun sast(path: String) {
		reports[yaml("sast")] = yaml(path)
	}

	@GitLabCiDsl
	fun secretDetection(path: String) {
		reports[yaml("secret_detection")] = yaml(path)
	}

	@GitLabCiDsl
	fun terraform(path: String) {
		reports[yaml("terraform")] = yaml(path)
	}

	//endregion

	override fun toYaml(): Yaml {
		val data = HashMap<Yaml, Yaml>()

		data[yaml("paths")] = yamlList(includes)
		data[yaml("exclude")] = yamlList(excludes)

		if (expireIn != null)
			data[yaml("expire_in")] = yaml(expireIn)

		if (exposeAs != null)
			data[yaml("expose_as")] = yaml(exposeAs)

		if (name != null)
			data[yaml("name")] = yaml(name)

		data[yaml("reports")] = yamlMap(reports)

		data[yaml("when")] = yaml(rule)

		return yamlMap(data)
	}

}
