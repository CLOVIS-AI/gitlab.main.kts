package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml

class Cache : YamlExport {
	internal var key: String? = null
	internal var keyFiles = ArrayList<String>()
	internal var keyPrefix: String? = null

	internal var includePaths = ArrayList<String>()
	internal var includeUntracked = false

	internal var only = "on_success"
	internal var policy = "pull-push"

	override fun toYaml(): Yaml {
		val elements = HashMap<Yaml, Yaml>()

		elements[yaml("key")] = when {
			keyFiles.isNotEmpty() -> yaml(
				yaml("files") to yaml(
					keyFiles.map { yaml(it) }
				),
			).let {
				if (keyPrefix != null)
					yaml(
						it.contents + (yaml("prefix") to yaml(keyPrefix!!)),
					)
				else
					it
			}
			else -> yaml(key ?: "default")
		}

		if (includePaths.isNotEmpty()) elements[yaml("paths")] =
			yaml(includePaths.map { yaml(it) })

		elements[yaml("when")] = yaml(only)
		elements[yaml("policy")] = yaml(policy)
		elements[yaml("untracked")] = yaml(includeUntracked)

		return yaml(elements)
	}
}

fun Job.cache(key: String? = null, block: Cache.() -> Unit) {
	Cache().apply { this.key = key }.apply(block).also { cache += it }
}

fun Job.cache(vararg files: String, prefix: String? = null, block: Cache.() -> Unit) {
	Cache()
		.apply {
			this.keyFiles += files
			this.keyPrefix = prefix
		}.apply(block)
		.also { cache += it }
}

fun Cache.include(path: String) {
	includePaths += path
}

fun Cache.includeUntrackedFiles() {
	includeUntracked = true
}

fun Cache.excludeUntrackedFiles() {
	includeUntracked = false
}

fun Cache.onlyOnSuccess() {
	only = "on_success"
}

fun Cache.onlyOnFailure() {
	only = "on_failure"
}

fun Cache.always() {
	only = "always"
}

fun Cache.pullPolicy() {
	policy = "pull"
}

fun Cache.pushPolicy() {
	policy = "push"
}

fun Cache.pullPushPolicy() {
	policy = "pull-push"
}
