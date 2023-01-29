package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

class Pacman private constructor(private val dsl: CommandDsl) {

	fun sync(vararg pkg: String) = with(dsl) {
		shell("pacman -Syuu ${pkg.joinToString(separator = " ")}")
	}

	companion object {
		val CommandDsl.pacman get() = Pacman(this)
	}
}
