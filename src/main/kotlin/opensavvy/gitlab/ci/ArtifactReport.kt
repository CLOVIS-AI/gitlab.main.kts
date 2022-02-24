package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml

class ArtifactReport : YamlExport {
	private var reports = HashMap<String, Yaml>()

	override fun toYaml() = Yaml.Collection.MapLiteral(
		reports.mapKeys { (key, _) -> Yaml.Scalar.StringLiteral(key) }
	)

	internal val isEmpty get() = reports.isEmpty()

	fun accessibility(report: String) {
		reports["accessibility"] = Yaml.Scalar.StringLiteral(report)
	}

	fun apiFuzzing(report: String) {
		reports["api_fuzzing"] = Yaml.Scalar.StringLiteral(report)
	}

	fun browserPerformance(report: String) {
		reports["browser_performance"] = Yaml.Scalar.StringLiteral(report)
	}

	fun clusterImageScanning(report: String) {
		reports["cluster_image_scanning"] = Yaml.Scalar.StringLiteral(report)
	}

	fun cobertura(report: String) {
		reports["cobertura"] = Yaml.Scalar.StringLiteral(report)
	}

	fun codeQuality(report: String) {
		reports["codequality"] = Yaml.Scalar.StringLiteral(report)
	}

	fun containerScanning(report: String) {
		reports["container_scanning"] = Yaml.Scalar.StringLiteral(report)
	}

	fun coverageFuzzing(report: String) {
		reports["coverage_fuzzing"] = Yaml.Scalar.StringLiteral(report)
	}

	fun dast(report: String) {
		reports["dast"] = Yaml.Scalar.StringLiteral(report)
	}

	fun dependencyScanning(report: String) {
		reports["dependency_scanning"] = Yaml.Scalar.StringLiteral(report)
	}

	fun dotenv(dotenvFile: String) {
		reports["dotenv"] = Yaml.Scalar.StringLiteral(dotenvFile)
	}

	fun junit(report: String) {
		reports["junit"] = Yaml.Scalar.StringLiteral(report)
	}

	fun licenseScanning(report: String) {
		reports["license_scanning"] = Yaml.Scalar.StringLiteral(report)
	}

	fun loadPerformance(report: String) {
		reports["load_performance"] = Yaml.Scalar.StringLiteral(report)
	}

	fun metrics(report: String) {
		reports["metrics"] = Yaml.Scalar.StringLiteral(report)
	}

	fun requirements(report: String) {
		reports["requirements"] = Yaml.Scalar.StringLiteral(report)
	}

	fun sast(report: String) {
		reports["sast"] = Yaml.Scalar.StringLiteral(report)
	}

	fun secretDetection(report: String) {
		reports["secret_detection"] = Yaml.Scalar.StringLiteral(report)
	}

	fun terraform(report: String) {
		reports["terraform"] = Yaml.Scalar.StringLiteral(report)
	}
}

fun Artifact.reports(block: ArtifactReport.() -> Unit) {
	reports.apply(block)
}
