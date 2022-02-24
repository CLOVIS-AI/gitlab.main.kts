package opensavvy.gitlab.ci.script

fun CommandDsl.gradlew(vararg tasks: String) {
	shell("./gradlew ${tasks.joinToString(separator = " ")}")
}
