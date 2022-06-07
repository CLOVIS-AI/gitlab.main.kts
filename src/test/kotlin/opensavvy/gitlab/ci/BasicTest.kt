package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.plugins.buildDockerfile
import opensavvy.gitlab.ci.plugins.publishChangelogToDiscord
import opensavvy.gitlab.ci.plugins.publishChangelogToTelegram
import opensavvy.gitlab.ci.plugins.publishDocker
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

				buildDockerfile("client/Dockerfile", jsChromeImage, "client")
			}

			val dockerJsChromeLatest by job {
				stage = deploy
				waitFor(dockerJsChrome)

				publishDocker(jsChromeImage, "latest")
			}

			val telegram by job {
				stage = deploy
				waitForNoOne()

				publishChangelogToTelegram()
			}

			val discord by job {
				stage = deploy
				waitForNoOne()

				publishChangelogToDiscord()
			}
		}.println()
	}
}
