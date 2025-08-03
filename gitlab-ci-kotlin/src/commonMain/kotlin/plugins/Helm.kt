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

package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

/**
 * Integrate [Helm](https://helm.sh/) into your build.
 *
 * This plugin adds the [helm] extension to scripts:
 * ```kotlin
 * val deploy by job {
 *     script {
 *         helm.addRepository("mongodb", "https://mongodb.github.io/helm-charts")
 *         helm.updateRepositories()
 *         helm.deploy("community-operator", "mongodb/community-operator")
 *     }
 * }
 * ```
 */
class Helm private constructor(private val dsl: CommandDsl) {

	/**
	 * Adds a Helm repository.
	 *
	 * ```kotlin
	 * script {
	 *     helm.addRepository("mongodb", "https://mongodb.github.io/helm-charts")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://helm.sh/docs/helm/helm_repo_add/)
	 */
	fun addRepository(name: String, url: String) = with(dsl) {
		shell("helm repo add $name $url")
	}

	/**
	 * Updates all repositories.
	 *
	 * ```kotlin
	 * script {
	 *     helm.updateRepositories()
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://helm.sh/docs/helm/helm_repo_update/)
	 */
	fun updateRepositories() = with(dsl) {
		shell("helm repo update")
	}

	/**
	 * Deploys the [chart] as [release].
	 *
	 * ```kotlin
	 * script {
	 *     helm.deploy("community-operator", "mongodb/community-operator")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://helm.sh/docs/helm/helm_upgrade/)
	 */
	fun deploy(
		release: String,
		chart: String,
		values: List<String> = emptyList(),
		namespace: String? = null,
		createNamespace: Boolean = namespace != null,
		wait: Boolean = true,
		atomic: Boolean = true,
	) = with(dsl) {
		val args = buildList {
			for (value in values) {
				add("-f $value")
			}

			add("--create-namespace=$createNamespace")
			add("--namespace=$namespace")

			if (wait)
				add("--wait")

			if (atomic)
				add("--atomic")
		}

		shell("helm upgrade --install $release $chart ${args.joinToString(separator = " ")}")
	}

	companion object {
		/**
		 * Accesses Helm commands.
		 *
		 * To use this plugin, the `helm` command must be available to this job.
		 *
		 * @see Helm
		 */
		val CommandDsl.helm get() = Helm(this)
	}
}
