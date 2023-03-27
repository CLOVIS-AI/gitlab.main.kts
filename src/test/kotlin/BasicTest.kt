package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradlew
import opensavvy.gitlab.ci.script.shell
import kotlin.test.Test

class BasicTest {

	@Test
	fun basicTest() {
		@Suppress("UNUSED_VARIABLE")
		gitlabCi {
			val build by stage()
			val test by stage()
			val publish by stage()

			val modules = listOf("logger", "backbone")
			fun Job.publish(module: String, publication: String, repository: String) {
				image("archlinux:base")

				script {
					shell("pacman -Syu --noconfirm git jre-openjdk-headless")
					gradlew.task("$module:publish${publication}PublicationTo${repository}Repository")
				}
			}

			if (Value.isDefaultBranch || Value.isTag) {
				for (module in modules) {
					job("$module:publish", stage = publish) {
						publish(module, "KotlinMultiplatform", "GitLab")
					}
				}
			}

			val dokka by job(stage = build) {
				script {
					gradlew.task("dokkaHtmlMultiModule")
					shell("mv build/dokka/htmlMultiModule documentation")
				}

				artifacts {
					name("Documentation")
					exposeAs("Documentation")

					include("documentation")
				}
			}

			if (Value.isDefaultBranch) {
				val pages by job(stage = publish) {
					dependsOn(dokka, artifacts = true)

					script {
						shell("mkdir -p public")
						shell("mv documentation public")
					}

					artifacts {
						include("public")
					}
				}
			}
		}.println()
	}
}
