package opensavvy.gitlab.ci.script

import opensavvy.gitlab.ci.*

fun Job.useDocker() {
	image("docker:19.03")
	service("docker:dind")
	tag("docker")
}

fun Job.useContainerRegistry() {
	useDocker()

	beforeScript {
		dockerLogInToGitLabRegistry()
	}
}

fun CommandDsl.dockerLogIn(username: String, password: String, registry: String) {
	shell("echo -n $password | docker login -u $username --password-stdin $registry")
}

fun CommandDsl.dockerLogInToGitLabRegistry() {
	dockerLogIn(Variables.Registry.username, Variables.Registry.password, Variables.Registry.server)
}

fun CommandDsl.dockerPull(image: String, allowFailure: Boolean = false) {
	shell("docker pull $image ${if (allowFailure) "|| true" else ""}")
}

fun CommandDsl.dockerBuild(
	image: String,
	dockerfile: String = "Dockerfile",
	context: String = ".",
	previousVersions: List<String> = emptyList(),
) {
	shell(
		"docker build " +
				"--pull " +
				"${previousVersions.joinToString(separator = " ") { "--cache-from $it" }} " +
				"--tag $image " +
				"-f $dockerfile " +
				context
	)
}

fun CommandDsl.dockerPush(image: String) {
	shell("docker push $image")
}

fun CommandDsl.dockerBuildAndPush(imageName: String, dockerfile: String = "Dockerfile", context: String = ".") {
	val latest = "$imageName:latest"
	val build = "$imageName:build-${Variables.Pipeline.iid}"

	dockerPull(latest, allowFailure = true)
	dockerBuild(build, dockerfile, context, previousVersions = listOf(latest))
	dockerPush(build)
}

fun CommandDsl.dockerRename(imageName: String, newVersion: String = "latest") {
	val current = "$imageName:build-\$CI_PIPELINE_IID"
	val new = "$imageName:$newVersion"

	dockerPull(current)
	shell("docker tag $current $new")
	dockerPush(new)
}
