package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import java.io.File

class Artifact : YamlExport {
	internal var includedPaths = ArrayList<String>()
	internal var excludedPaths = ArrayList<String>()
	internal var expireIn: String? = null
	internal var exposeAs: String? = null
	internal var name: String? = null
	internal var reports = ArtifactReport()
	internal var includeUntracked = false
	internal var only = "on_success"

	override fun toYaml(): Yaml {
		val elements = HashMap<Yaml, Yaml>()

		if (includedPaths.isNotEmpty())
			elements[Yaml.Scalar.StringLiteral("paths")] = Yaml.Collection.ListLiteral(
				includedPaths.map { Yaml.Scalar.StringLiteral(it) }
			)

		if (excludedPaths.isNotEmpty())
			elements[Yaml.Scalar.StringLiteral("exclude")] = Yaml.Collection.ListLiteral(
				excludedPaths.map { Yaml.Scalar.StringLiteral(it) }
			)

		if (expireIn != null)
			elements[Yaml.Scalar.StringLiteral("expire_in")] = Yaml.Scalar.StringLiteral(expireIn!!)

		if (exposeAs != null)
			elements[Yaml.Scalar.StringLiteral("expose_as")] = Yaml.Scalar.StringLiteral(exposeAs!!)

		if (name != null)
			elements[Yaml.Scalar.StringLiteral("name")] = Yaml.Scalar.StringLiteral(name!!)

		if (!reports.isEmpty)
			elements[Yaml.Scalar.StringLiteral("reports")] = reports.toYaml()

		elements[Yaml.Scalar.StringLiteral("when")] = Yaml.Scalar.StringLiteral(only)
		elements[Yaml.Scalar.StringLiteral("untracked")] = Yaml.Scalar.BooleanLiteral(includeUntracked)

		return Yaml.Collection.MapLiteral(elements)
	}
}

fun Job.artifacts(block: Artifact.() -> Unit) {
	artifact.apply(block)
}

fun Artifact.include(file: File) {
	includedPaths += file.path
}

fun Artifact.include(glob: String) {
	includedPaths += glob
}

fun Artifact.includeUntrackedFiles() {
	includeUntracked = true
}

fun Artifact.exclude(file: File) {
	excludedPaths += file.path
}

fun Artifact.exclude(glob: String) {
	excludedPaths += glob
}

fun Artifact.excludeUntrackedFiles() {
	includeUntracked = false
}

fun Artifact.doNotExpire() {
	expireIn = "never"
}

fun Artifact.expireIn(time: String) {
	expireIn = time
}

fun Artifact.exposeAs(name: String) {
	exposeAs = name
}

fun Artifact.name(name: String) {
	this.name = name
}

fun Artifact.onlyOnSuccess() {
	only = "on_success"
}

fun Artifact.onlyOnFailure() {
	only = "on_failure"
}

fun Artifact.always() {
	only = "always"
}
