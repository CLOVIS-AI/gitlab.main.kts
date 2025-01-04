package opensavvy.gitlab.ci.diff

import opensavvy.gitlab.ci.YamlExport
import java.io.File
import kotlin.test.assertEquals

private val testRoot = File("src/test/resources")
private val overrideOutputs = System.getenv("tests_override_results").toBoolean()

fun assertEqualsFile(filename: String, data: String) {
	val file = File(testRoot, filename)

	if (overrideOutputs) {
		val parent = file.parentFile

		if (!parent.exists()) {
			println("TRACE [File diff] Creating the parent directories…")
			parent.mkdirs()
		}

		println("TRACE [File diff] Writing the output to the file…")
		file.writeText(data)
	} else {
		assertEquals(
			file.readText(),
			data,
			"The test results are different from the previous execution (from file://${file.absolutePath})"
		)
	}
}

fun assertEqualsFile(filename: String, data: YamlExport) =
	assertEqualsFile(filename, data.toYaml().toYamlString())
