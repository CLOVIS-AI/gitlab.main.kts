package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
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
			elements[yaml("paths")] = yaml(includedPaths.map { yaml(it) })

		if (excludedPaths.isNotEmpty())
			elements[yaml("exclude")] = yaml(excludedPaths.map { yaml(it) })

		if (expireIn != null)
			elements[yaml("expire_in")] = yaml(expireIn!!)

		if (exposeAs != null)
			elements[yaml("expose_as")] = yaml(exposeAs!!)

		if (name != null)
			elements[yaml("name")] = yaml(name!!)

		if (!reports.isEmpty)
			elements[yaml("reports")] = reports.toYaml()

		elements[yaml("when")] = yaml(only)
		elements[yaml("untracked")] = yaml(includeUntracked)

		return yaml(elements)
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
