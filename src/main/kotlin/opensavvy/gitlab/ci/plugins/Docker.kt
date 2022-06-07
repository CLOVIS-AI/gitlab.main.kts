package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.Job
import opensavvy.gitlab.ci.script
import opensavvy.gitlab.ci.script.dockerBuildAndPush
import opensavvy.gitlab.ci.script.dockerRename
import opensavvy.gitlab.ci.script.useContainerRegistry
import opensavvy.gitlab.ci.script.useDocker

fun Job.buildDockerfile(dockerfile: String, imageName: String, context: String = ".") {
	useDocker()
	useContainerRegistry()

	script {
		dockerBuildAndPush(imageName, dockerfile, context)
	}
}

fun Job.publishDocker(imageName: String, newVersion: String) {
	useDocker()
	useContainerRegistry()

	script {
		dockerRename(imageName, newVersion)
	}
}
