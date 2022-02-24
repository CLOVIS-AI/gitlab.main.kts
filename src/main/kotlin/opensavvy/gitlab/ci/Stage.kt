package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.utils.generateReadOnlyDelegateProvider

data class Stage(
	val name: String,
)

fun GitLabCi.stage(name: String): Stage = Stage(name)
	.also { stages += it }

fun GitLabCi.stage() = generateReadOnlyDelegateProvider { parent, property ->
	stage(property.name)
}
