/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

import opensavvy.gitlab.ci.GitLabCi
import opensavvy.gitlab.ci.Job
import opensavvy.gitlab.ci.Stage
import opensavvy.gitlab.ci.job
import opensavvy.gitlab.ci.plugins.Docker.Companion.defaultVersion
import opensavvy.gitlab.ci.plugins.Kaniko.Companion.kanikoBuild
import opensavvy.gitlab.ci.plugins.Kaniko.Companion.kanikoRename
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

/**
 * Integrates [Kaniko](https://github.com/GoogleContainerTools/kaniko) into your builds.
 *
 * Kaniko allows building Docker containers without needing root rights on the machine, allowing it to be used
 * within other containers. Since CI jobs usually are already containerized, Kaniko is a much simpler way of building
 * containers than using [Docker in Docker][Docker].
 *
 * ### Usage
 *
 * This examples:
 * - Builds an image `"app"` from the Dockerfile in folder `docker`, with a temporary version,
 * - Publishes it to the GitLab registry,
 * - Runs tests on it,
 * - If we're in the main branch and the tests are successful, retags the image as `"latest"`.
 *
 * ```kotlin
 * gitLabCi {
 *     val build by stage()
 *     val test by stage()
 *     val deploy by stage()
 *
 *     val imageName = "${Variable.Registry.image}/app"
 *
 *     val buildApp by kanikoBuild(
 *         imageName = imageName,
 *         context = "docker",
 *         stage = build,
 *     )
 *
 *     val testApp by job(stage = test) {
 *         image(imageName, version = Docker.defaultVersion)
 *
 *         script {
 *             // …
 *         }
 *     }
 *
 *     if (Value.isDefaultBranch) {
 *         val publishApp by kanikoRename(
 *             imageName = imageName,
 *             stage = deploy,
 *         )
 *     }
 * }
 * ```
 *
 * ### Extensions
 *
 * Pipeline extensions:
 * - [kanikoBuild]: Builds an image and pushes it to a registry.
 * - [kanikoRename]: Renames an image to pushes it to a registry.
 */
class Kaniko private constructor(private val dsl: CommandDsl) {

	/**
	 * Logs into the GitLab container registry for Kaniko.
	 */
	fun logInToRegistry() = with(dsl) {
		shell($$"""
			echo "{
				\"auths\": {
					\"$CI_REGISTRY\": {
						\"auth\": \"$(printf "%s:%s" "$CI_REGISTRY_USER" "$CI_REGISTRY_PASSWORD" | base64 | tr -d "\n")\"
					},
					\"$(echo -n $CI_DEPENDENCY_PROXY_SERVER | awk -F[:] "{print \$2} ")\": {
						\"auth\": \"$(printf "%s:%s" "$CI_DEPENDENCY_PROXY_USER" "$CI_DEPENDENCY_PROXY_PASSWORD" | base64 | tr -d "\n")\"
					}
				}	
			}" >/kaniko/.docker/config.json
		""".trimIndent().replace("\n", ""))
	}

	/**
	 * Builds an image called [imageName] of version [imageVersion] based on the [context] directory and the specified [dockerfile].
	 *
	 * If [imageName] contains a registry name, it is pushed to that registry.
	 *
	 * Use [kanikoBuild] for a already built job.
	 */
	fun build(
		imageName: String,
		imageVersion: String = defaultVersion,
		context: String = ".",
		dockerfile: String = "$context/Dockerfile",
	) = with(dsl) {
		shell($$"""
			/kaniko/executor --context "$$context" --dockerfile "$$dockerfile" --cache --registry-mirror "$CI_DEPENDENCY_PROXY_GROUP_IMAGE_PREFIX" --destination "$$imageName:$$imageVersion"
		""".trimIndent())
	}

	companion object {

		/**
		 * Creates a job that builds the image [imageName] with version [imageVersion].
		 * If the [imageName] contains a registry name, pushes the image to the registry.
		 *
		 * This job is automatically configured to communicate with the GitLab container registry.
		 */
		fun GitLabCi.kanikoBuild(
			imageName: String,
			imageVersion: String = defaultVersion,
			context: String = ".",
			dockerfile: String = "$context/Dockerfile",
			jobName: String? = null,
			stage: Stage? = null,
			block: Job.() -> Unit = {},
		) = job(jobName, stage) {
			image("gcr.io/kaniko-project/executor", "v1.23.2-debug") {
				entrypoint = listOf("")
			}

			// https://github.com/GoogleContainerTools/kaniko/issues/2751
			variable("http2client", "0")

			beforeScript {
				kaniko.logInToRegistry()
			}

			script {
				kaniko.build(
					imageName = imageName,
					imageVersion = imageVersion,
					context = context,
					dockerfile = dockerfile,
				)
			}

			block()
		}

		/**
		 * Creates a job that changes the version of the image [imageName] from [oldVersion] to [newVersion].
		 *
		 * If the image names contain registry urls, the images are pull/pushed to the respective registry.
		 */
		fun GitLabCi.kanikoRename(
			imageName: String,
			oldVersion: String = defaultVersion,
			newVersion: String = "latest",
			jobName: String? = null,
			stage: Stage? = null,
			block: Job.() -> Unit = {},
		) = job(jobName, stage) {
			image("gcr.io/go-containerregistry/crane", "debug") {
				entrypoint = listOf("")
			}

			script {
				shell($$"""crane auth login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY""")
				shell($$"""crane tag "$$imageName:$$oldVersion" "$$newVersion"""")
			}

			block()
		}

		val CommandDsl.kaniko: Kaniko get() = Kaniko(this)

	}
}
