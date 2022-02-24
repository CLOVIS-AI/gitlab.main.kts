package opensavvy.gitlab.ci.script

import opensavvy.gitlab.ci.YamlExport
import opensavvy.gitlab.ci.yaml.Yaml
import org.intellij.lang.annotations.Language

abstract class Command : YamlExport

data class Shell(@Language("Sh") val command: String) : Command() {
	override fun toYaml() = Yaml.Scalar.StringLiteral(command)
}

fun CommandDsl.shell(@Language("Sh") command: String) = Shell(command)
	.also { +it }
