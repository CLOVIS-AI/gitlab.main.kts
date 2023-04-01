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
				keyFile("gradle/wrapper/gradle.properties")
			}
		}

		val CommandDsl.gradle get() = Gradle(this, isWrapper = false)
		val CommandDsl.gradlew get() = Gradle(this, isWrapper = true)
	}
}
