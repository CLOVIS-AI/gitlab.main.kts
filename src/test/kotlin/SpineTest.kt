package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradlew
import opensavvy.gitlab.ci.plugins.Gradle.Companion.useGradle
import opensavvy.gitlab.ci.plugins.Pacman.Companion.pacman
import opensavvy.gitlab.ci.script.shell
import kotlin.test.Test

// Inspired by https://gitlab.com/opensavvy/pedestal/-/blob/main/.gitlab-ci.yml

class SpineTest {

	@Test
	fun spine() {
		val pipeline = gitlabCi {
			val test by stage()
			val publish by stage()

			val modules =
				listOf("logger", "state", "cache", "backbone", "spine", "spine-ktor", "spine-ktor", "spine-ktor-client")

			for (module in modules) {
				val build = job("$module:test:jvm", stage = test) {
					useGradle()

					image("openjdk", "latest")

					script {
						gradlew.tasks("$module:koverReport", "$module:koverVerify")
					}

					artifacts {
						include("test-report-$module")
						exposeAs("Test and coverage report")
						junit("$module/build/test-results/jvmTest/**.xml")
					}
				}

				job("$module:test:jvm-coverage", stage = test) {
					image("registry.gitlab.com/haynes/jacoco2cobertura", "1.0.8")
					dependsOn(build, artifacts = true)

					script {
						shell("python /opt/cover2cover.py test-report-$module/coverage.xml ${Variable.buildDir}/$module/src/main/kotlin >cobertura.xml")
					}

					artifacts {
						coverage("cobertura", "cobertura.xml")
					}
				}
			}

			if (Value.isTag) {
				job("publish", stage = publish) {
					useGradle()

					image("archlinux", "base")

					beforeScript {
						pacman.sync("git", "jre-openjdk-headless")
						shell("export JAVA_HOME=\$(dirname \$(dirname \$(readlink -f \$(command -v java))))")
					}

					script {
						gradlew.tasks(modules.map { "$it:publishToGitLab" })
					}
				}
			}

			val dokka by job {
				image("openjdk")

				script {
					gradlew.task("dokkaHtmlMultiModule")
					shell("mv build/dokka/htmlMultiModule documentation")
				}

				artifacts {
					include("documentation")
					exposeAs("Documentation")
				}
			}

			if (Value.isTag) {
				job("pages", stage = publish) {
					image("alpine")

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
		}

		pipeline.println()
	}

}
