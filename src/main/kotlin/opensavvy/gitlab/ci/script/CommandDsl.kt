package opensavvy.gitlab.ci.script

class CommandDsl(private val list: MutableList<Command>) {

	fun clear() {
		list.clear()
	}

	operator fun Command.unaryPlus() {
		list.add(this@unaryPlus)
	}

}
