package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

class Helm private constructor(private val dsl: CommandDsl) {

	fun addRepository(name: String, url: String) = with(dsl) {
		shell("helm repo add $name $url")
	}

	fun updateRepositories() = with(dsl) {
		shell("helm repo update")
	}

	fun deploy(
		release: String,
		chart: String,
		values: List<String> = emptyList(),
		namespace: String? = null,
		createNamespace: Boolean = namespace != null,
		wait: Boolean = true,
		atomic: Boolean = true,
	) = with(dsl) {
		val args = buildList {
			for (value in values) {
				add("-f $value")
			}

			add("--create-namespace=$createNamespace")
			add("--namespace=$namespace")

			if (wait)
				add("--wait")

			if (atomic)
				add("--atomic")
		}

		shell("helm upgrade --install $release $chart ${args.joinToString(separator = " ")}")
	}

	companion object {
		val CommandDsl.helm get() = Helm(this)
	}
}
