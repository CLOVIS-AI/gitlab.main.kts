package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.Job
import opensavvy.gitlab.ci.Variable
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

class Docker private constructor(private val dsl: CommandDsl) {

	fun logInToRegistry(registry: String, username: String, password: String) = with(dsl) {
		shell("echo -n $password | docker login -u $username --password-stdin $registry")
	}

	fun pull(image: String, version: String, allowFailure: Boolean = false) = with(dsl) {
		shell("docker pull $image:$version ${if (allowFailure) "|| true" else ""}")
	}

	fun build(
		image: String,
		version: String = "build-${Variable.MergeRequest.iid}",
		dockerfile: String = "Dockerfile",
		context: String = ".",
		previousVersions: List<String> = listOf("latest"),
	) = with(dsl) {
		val cacheArgs = if (previousVersions.isNotEmpty()) {
			"--build-arg BUILDKIT_INLINE_CACHE=1 ${previousVersions.joinToString(separator = " ") { "--cache-from $image:$it" }}"
		} else ""


		shell("docker build --pull $cacheArgs --tag $image:$version -f $dockerfile $context")
	}

	fun push(
		image: String,
		version: String = "build-${Variable.MergeRequest.iid}",
	) = with(dsl) {
		shell("docker push $image:$version")
	}

	fun rename(
		image: String,
		oldVersion: String = "build-${Variable.MergeRequest.iid}",
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
		 */
		fun Job.useDockerInDocker() {
			image("docker", version = "20.10")
			service("docker", version = "20.10-dind")
			tag("docker")
		}

		/**
		 * Logs in the current job to the GitLab Container Registry.
		 *
		 * For more information, see the [GitLab documentation](https://docs.gitlab.com/ee/user/packages/container_registry/).
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
		 */
		val CommandDsl.docker get() = Docker(this)
	}
}
