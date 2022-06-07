package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.script.dockerBuildAndPush
import opensavvy.gitlab.ci.script.dockerRename
import opensavvy.gitlab.ci.script.useDockerInDocker
import opensavvy.gitlab.ci.script.useGitLabRegistry
import kotlin.test.Test

class BasicTest {

	@Test
	fun basicTest() {
		@Suppress("UNUSED_VARIABLE")
		gitlabCi {
			val jsChromeImage = "\$CI_REGISTRY_IMAGE/js-chrome"

			val docker by stage()
			val deploy by stage()

			val dockerJsChrome by job {
				stage = docker
				useDockerInDocker()
				useGitLabRegistry()

				script {
					dockerBuildAndPush(
						jsChromeImage,
						"client/build.dockerfile",
						"client"
					)
				}
			}

			val dockerJsChromeLatest by job {
				stage = deploy
				useDockerInDocker()
				useGitLabRegistry()
				waitFor(dockerJsChrome)

				script {
					dockerRename(jsChromeImage)
				}
			}
		}.println()
	}
}
