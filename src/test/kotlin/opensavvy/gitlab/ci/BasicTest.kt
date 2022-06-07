package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.script.*
import kotlin.test.Test

class BasicTest {

	@Test
	fun basicTest() {
		@Suppress("UNUSED_VARIABLE")
		gitlabCi {
			val jsChromeImage = "${Variables.Registry.image}/js-chrome"

			val docker by stage()
			val deploy by stage()

			val dockerJsChrome by job {
				stage = docker
				useDocker()
				useContainerRegistry()
				waitForNoOne()

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
				useDocker()
				useContainerRegistry()
				waitFor(dockerJsChrome)

				script {
					dockerRename(jsChromeImage)
				}
			}
		}.println()
	}
}
