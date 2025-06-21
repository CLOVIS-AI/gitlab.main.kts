/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.Value
import opensavvy.gitlab.ci.diff.assertEqualsFile
import opensavvy.gitlab.ci.gitlabCi
import opensavvy.gitlab.ci.plugins.Kaniko.Companion.kanikoBuild
import opensavvy.gitlab.ci.plugins.Kaniko.Companion.kanikoRename
import opensavvy.gitlab.ci.stage
import opensavvy.prepared.runner.testballoon.preparedSuite

val KanikoTest by preparedSuite {

	test("Build and publish image") {
		val pipeline = gitlabCi {
			val build by stage()
			val deploy by stage()

			val image = "${Value.Registry.image}/minecraft"

			val kanikoBuild by kanikoBuild(
				imageName = image,
				context = "docker",
				stage = build,
			)

			if (Value.isTag || Value.isDefaultBranch) {
				val kanikoRename by kanikoRename(
					imageName = image,
					stage = deploy,
				) {
					dependsOn(kanikoBuild)
				}
			}
		}

		assertEqualsFile("plugins/kaniko/build.yaml", pipeline)
	}

}
