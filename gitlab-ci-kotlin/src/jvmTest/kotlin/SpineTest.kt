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

package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.diff.assertEqualsFile
import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradlew
import opensavvy.gitlab.ci.plugins.Gradle.Companion.useGradle
import opensavvy.gitlab.ci.plugins.Pacman.Companion.pacman
import opensavvy.gitlab.ci.script.shell
import opensavvy.prepared.runner.kotest.PreparedSpec

// Inspired by https://gitlab.com/opensavvy/pedestal/-/blob/main/.gitlab-ci.yml

class SpineTest : PreparedSpec({

	test("Generate a pipeline inspired by the old Spine") {
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

		assertEqualsFile("spine.gitlab-ci.yml", pipeline)
	}

})
