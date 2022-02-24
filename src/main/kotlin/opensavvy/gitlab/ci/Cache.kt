package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml

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

		elements[Yaml.Scalar.StringLiteral("key")] = when {
			keyFiles.isNotEmpty() -> Yaml.Collection.MapLiteral(
				Yaml.Scalar.StringLiteral("files") to Yaml.Collection.ListLiteral(
					keyFiles.map { Yaml.Scalar.StringLiteral(it) }
				),
			).let {
				if (keyPrefix != null)
					Yaml.Collection.MapLiteral(
						it.contents + (Yaml.Scalar.StringLiteral("prefix") to Yaml.Scalar.StringLiteral(keyPrefix!!)),
					)
				else
					it
			}
			else -> Yaml.Scalar.StringLiteral(key ?: "default")
		}

		if (includePaths.isNotEmpty()) elements[Yaml.Scalar.StringLiteral("paths")] =
			Yaml.Collection.ListLiteral(includePaths.map { Yaml.Scalar.StringLiteral(it) })

		elements[Yaml.Scalar.StringLiteral("when")] = Yaml.Scalar.StringLiteral(only)
		elements[Yaml.Scalar.StringLiteral("policy")] = Yaml.Scalar.StringLiteral(policy)
		elements[Yaml.Scalar.StringLiteral("untracked")] = Yaml.Scalar.BooleanLiteral(includeUntracked)

		return Yaml.Collection.MapLiteral(elements)
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
