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
import opensavvy.gitlab.ci.script.shell
import opensavvy.prepared.runner.testballoon.preparedSuite

val BasicTest by preparedSuite {

	test("Generate a basic CI inspired by Pedestal") {
		val pipeline = gitlabCi {
			val build by stage()
			val test by stage()
			val publish by stage()

			val modules = listOf("logger", "backbone")
			fun Job.publish(module: String, publication: String, repository: String) {
				image("archlinux")

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
		}

		assertEqualsFile("basic.gitlab-ci.yaml", pipeline)
	}

}
