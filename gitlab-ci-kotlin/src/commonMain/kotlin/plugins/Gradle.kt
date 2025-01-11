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

package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.Job
import opensavvy.gitlab.ci.Variable
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

class Gradle private constructor(private val dsl: CommandDsl, private val isWrapper: Boolean) {

	private val cmd = if (isWrapper)
		"./gradlew"
	else
		"gradle"

	fun task(task: String) = tasks(task)

	fun tasks(vararg task: String) = with(dsl) {
		shell("$cmd ${task.joinToString(" ")}")
	}

	fun tasks(task: Iterable<String>) = tasks(*task.toList().toTypedArray())

	companion object {
		fun Job.useGradle() {
			variable("GRADLE_USER_HOME", "${Variable.buildDir}/.gradle")

			cache {
				include(".gradle/wrapper")
				keyFile("gradle/wrapper/gradle-wrapper.properties")
			}
		}

		val CommandDsl.gradle get() = Gradle(this, isWrapper = false)
		val CommandDsl.gradlew get() = Gradle(this, isWrapper = true)
	}
}
