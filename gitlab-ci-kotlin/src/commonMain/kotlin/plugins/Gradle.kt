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
import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradle
import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradlew
import opensavvy.gitlab.ci.plugins.Gradle.Companion.useGradle
import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

/**
 * Plugin to use the Gradle build tool within pipelines.
 *
 * This plugin automatically configures wrapper caching and test reporting.
 *
 * ### Example
 *
 * ```kotlin
 * gitlabCi {
 *     val build by job {
 *         useGradle()
 *
 *         script {
 *             gradlew.task("build")
 *         }
 *     }
 * }
 * ```
 *
 * @see useGradle Enable this plugin.
 * @see task Run a task.
 */
class Gradle private constructor(private val dsl: CommandDsl, private val isWrapper: Boolean) {

	private val cmd = if (isWrapper)
		"./gradlew"
	else
		"gradle"

	/**
	 * Executes the Gradle task named [task].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * gitlabCi {
	 *     val build by job {
	 *         useGradle()
	 *
	 *         script {
	 *             gradlew.task("build")
	 *         }
	 *     }
	 * }
	 * ```
	 */
	fun task(task: String) = tasks(task)

	/**
	 * Executes the Gradle tasks named [tasks].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * gitlabCi {
	 *     val build by job {
	 *         useGradle()
	 *
	 *         script {
	 *             gradlew.tasks("assemble", "check")
	 *         }
	 *     }
	 * }
	 * ```
	 */
	fun tasks(vararg tasks: String) = with(dsl) {
		shell("$cmd ${tasks.joinToString(" ")}")
	}

	/**
	 * Executes the Gradle tasks named [tasks].
	 */
	fun tasks(tasks: Iterable<String>) = tasks(*tasks.toList().toTypedArray())

	companion object {

		/**
		 * Enables the [Gradle] plugin.
		 */
		fun Job.useGradle() {
			variable("GRADLE_USER_HOME", "${Variable.buildDir}/.gradle")

			cache {
				include(".gradle/wrapper")
				keyFile("gradle/wrapper/gradle-wrapper.properties")
			}

			artifacts {
				junit("'**/build/test-results/**/TEST-*.xml'")
			}

			// Kover's :koverLog format
			coverage("application line coverage: (\\d+\\.?\\d*%)")
		}

		/**
		 * Allows executing commands using the system `gradle` command.
		 *
		 * @see gradlew Use the Gradle Wrapper bundled in the project.
		 * @see Gradle.task Execute a task.
		 */
		val CommandDsl.gradle get() = Gradle(this, isWrapper = false)

		/**
		 * Allows executing commands using the Gradle Wrapper bundled into the project.
		 *
		 * @see gradle Use the system `gradle` command.
		 * @see Gradle.task Execute a task.
		 */
		val CommandDsl.gradlew get() = Gradle(this, isWrapper = true)
	}
}
