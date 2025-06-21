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

import opensavvy.gitlab.ci.Job
import opensavvy.gitlab.ci.Variable
import opensavvy.gitlab.ci.plugins.Docker.Companion.docker
import opensavvy.gitlab.ci.plugins.Docker.Companion.useContainerRegistry
import opensavvy.gitlab.ci.plugins.Docker.Companion.useDockerInDocker
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

/**
 * Integrate [Docker](https://www.docker.com/) into your build.
 *
 * This plugin is responsible for adding the [docker] extension to scripts:
 * ```kotlin
 * val dockerBuild by job {
 *     useDockerInDocker()
 *     useContainerRegistry()
 *
 *     script {
 *         docker.build("backend")
 *     }
 * }
 * ```
 *
 * This plugin uses [Docker in Docker](https://www.docker.com/resources/docker-in-docker-containerized-ci-workflows-dockercon-2023/),
 * which can create security vulnerabilities. To build containers without using Docker in Docker, see the [Kaniko] plugin.
 *
 * ## Job extensions
 *
 * This plugin provides the following job extensions:
 * - [useDockerInDocker]: configure the Docker In Docker service,
 * - [useContainerRegistry]: logs in to the GitLab Container Registry of the current project.
 */
class Docker private constructor(private val dsl: CommandDsl) {

	/**
	 * Logs in to an arbitrary container registry.
	 *
	 * To log in to the GitLab container registry of the current project, use [useContainerRegistry] instead.
	 *
	 * @param registry The URL of the registry to log in to.
	 * @param username The username used to log in to [registry].
	 * @param password The password used to log in to [registry].
	 */
	fun logInToRegistry(registry: String, username: String, password: String) = with(dsl) {
		shell("echo -n $password | docker login -u $username --password-stdin $registry")
	}

	/**
	 * Pulls a given [image]'s [version].
	 *
	 * Note that pulling a previous version of an image before building a new version is useless: Docker's cache is able
	 * to reuse layers without pulling the image.
	 *
	 * @param image The name of the image to pull (e.g. `"alpine"`, `"curlimages/curl"`).
	 * @param version The version to pull (e.g. `"edge"`).
	 * @param allowFailure If set to `true`, failing to pull the image will be ignored, and will not fail the current job.
	 */
	fun pull(image: String, version: String = "latest", allowFailure: Boolean = false) = with(dsl) {
		shell("docker pull $image:$version ${if (allowFailure) "|| true" else ""}")
	}

	/**
	 * Builds a Docker image from a [Dockerfile](https://docs.docker.com/engine/reference/builder/).
	 *
	 * The image will be built but not published anywhere, remember to call [push] after building it.
	 *
	 * The images built by this function have the [BuildKit Inline Cache](https://docs.docker.com/engine/reference/commandline/build/#cache-from)
	 * enabled, and can therefore be used as caching for future builds.
	 *
	 * @param image The name of the image that will be built (e.g. `"backend"`).
	 * @param version The version which will be assigned to the built image.
	 * By default, the version number is created from the pipeline identifier, to ensure two pipelines do not overwrite each other.
	 * @param dockerfile The path to the Dockerfile, relative to the current working directory.
	 * @param context The path of the directory in which the build takes place, relative to the current working directory.
	 * The files in this directory will be available in the Dockerfile's `COPY` instruction.
	 * @param previousVersions Version names of previous versions that are candidate for caching.
	 * If Docker finds identical layers in these images from what is currently being built, it will reuse the existing layers, greatly speeding the build.
	 * Note that for Docker to use an image as a cache layer, [it must have caching enabled](https://docs.docker.com/engine/reference/commandline/build/#cache-from).
	 *
	 * @sample opensavvy.gitlab.ci.plugins.DockerTest.buildImage
	 */
	fun build(
		image: String,
		version: String = defaultVersion,
		dockerfile: String = "Dockerfile",
		context: String = ".",
		previousVersions: List<String> = listOf("latest"),
	) = with(dsl) {
		val cacheArgs = if (previousVersions.isNotEmpty()) {
			"--build-arg BUILDKIT_INLINE_CACHE=1 ${previousVersions.joinToString(separator = " ") { "--cache-from $image:$it" }}"
		} else ""


		shell("docker build --pull $cacheArgs --tag $image:$version -f $dockerfile $context")
	}

	/**
	 * Pushes the [image] to a container registry.
	 *
	 * Before pushing to a container registry, you may want to [logInToRegistry].
	 *
	 * @param image The name of the image that will be pushed (e.g. `"backend"`).
	 * The image must exist locally.
	 * @param version The version of the image to push.
	 * The default version number for this command is the same as the [build] command's, making it easy to use them together.
	 *
	 * @sample opensavvy.gitlab.ci.plugins.DockerTest.buildImage
	 */
	fun push(
		image: String,
		version: String = defaultVersion,
	) = with(dsl) {
		shell("docker push $image:$version")
	}

	/**
	 * Renames a version of an [image].
	 *
	 * This command pulls the [image] for the version [oldVersion], renames it to [newVersion], then finally pushes it back.
	 *
	 * @param image The name of the image that will be renamed (e.g. `"backend"`).
	 * @param oldVersion The version that will be renamed.
	 * The default version number for this command is the same as the [build] command's, making it easy to use them together.
	 * @param newVersion The new name of the version.
	 *
	 * @sample opensavvy.gitlab.ci.plugins.DockerTest.buildImage
	 */
	fun rename(
		image: String,
		oldVersion: String = defaultVersion,
		newVersion: String = "latest",
	) = with(dsl) {
		pull(image, oldVersion)
		shell("docker tag $image:$oldVersion $image:$newVersion")
		push(image, newVersion)
	}

	companion object {
		/**
		 * Configures the current job to use the `docker` command using Docker In Docker.
		 *
		 * For more information, see the [GitLab documentation](https://docs.gitlab.com/ee/ci/docker/using_docker_build.html).
		 *
		 * @param dockerVersion The version of Docker to use, which must correspond to a tag of the [official docker image](https://hub.docker.com/_/docker).
		 * @param dockerInDockerVersion The version of the Docker, which must correspond to a tag of the [official docker image](https://hub.docker.com/_/docker) with DinD enabled.
		 *
		 * @sample opensavvy.gitlab.ci.plugins.DockerTest.buildImage
		 */
		fun Job.useDockerInDocker(
			dockerVersion: String = "20.10",
			dockerInDockerVersion: String = "$dockerVersion-dind",
		) {
			image("docker", version = dockerVersion)
			service("docker", version = dockerInDockerVersion)
			tag("docker")
		}

		/**
		 * Logs in the current job to the GitLab Container Registry.
		 *
		 * For more information, see the [GitLab documentation](https://docs.gitlab.com/ee/user/packages/container_registry/).
		 *
		 * @sample opensavvy.gitlab.ci.plugins.DockerTest.buildImage
		 */
		fun Job.useContainerRegistry() {
			beforeScript {
				docker.logInToRegistry(
					registry = Variable.Registry.server,
					username = Variable.Registry.username,
					password = Variable.Registry.password,
				)
			}
		}

		/**
		 * Accesses Docker commands.
		 *
		 * To use this plugin, the `docker` command must be available to this job.
		 * To automatically set it up, see [useDockerInDocker].
		 *
		 * @sample opensavvy.gitlab.ci.plugins.DockerTest.buildImage
		 */
		val CommandDsl.docker get() = Docker(this)

		/**
		 * Unique version used as a default value when unspecified.
		 */
		const val defaultVersion = "build-${Variable.MergeRequest.iid}"
	}
}
