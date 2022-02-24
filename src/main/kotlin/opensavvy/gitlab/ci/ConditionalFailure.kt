package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml

sealed class ConditionalFailure : YamlExport {
	data class Always(val allow: Boolean) : ConditionalFailure() {
		override fun toYaml() = Yaml.Scalar.BooleanLiteral(allow)
	}

	data class AllowWhenExitCode(val allowedCodes: Set<UByte>) : ConditionalFailure() {
		override fun toYaml() = Yaml.Collection.MapLiteral(
			Yaml.Scalar.StringLiteral("exit_codes") to Yaml.Collection.ListLiteral(
				allowedCodes.map { Yaml.Scalar.IntegerLiteral(it.toLong()) }
			)
		)
	}
}

fun Job.allowFailure() {
	allowFailure = ConditionalFailure.Always(true)
}

fun Job.forbidFailure() {
	allowFailure = ConditionalFailure.Always(false)
}

fun Job.allowFailureWithExitCode(code: UByte) {
	allowFailure = when (val allowFailure = allowFailure) {
		is ConditionalFailure.AllowWhenExitCode -> allowFailure.copy(allowFailure.allowedCodes + code)
		else -> ConditionalFailure.AllowWhenExitCode(setOf(code))
	}
}
