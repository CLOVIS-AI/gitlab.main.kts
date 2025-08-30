/*
 * Copyright (c) 2022-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlList
import opensavvy.gitlab.ci.yaml.yamlMap

/**
 * Configures job artifacts that are attached to the job when it succeeds, fails, or always.
 *
 * Job artifacts are a list of files and directories that are sent to GitLab after the job finishes.
 * They are available for download in the GitLab UI if the size is smaller than the maximum artifact size.
 * By default, jobs in later stages automatically download all the artifacts created by jobs in earlier stages.
 *
 * ### Example
 *
 * ```kotlin
 * val build by job {
 *     script {
 *         shell("make build")
 *     }
 *
 *     artifacts {
 *         include("binaries/")
 *         include(".config")
 *         expireIn("1 week")
 *     }
 * }
 * ```
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifacts)
 */
@GitLabCiDsl
class Artifacts : YamlExport {
	private val includes = ArrayList<String>()
	private val excludes = ArrayList<String>()

	private var expireIn: String? = null
	private var exposeAs: String? = null
	private var name: String? = null

	private val reports = HashMap<Yaml, Yaml>()

	private var rule = When.Always

	/**
	 * Specifies which files to save as job artifacts.
	 *
	 * Paths are relative to the project directory and can use wildcards with glob patterns.
	 * You can call this method multiple times to include multiple paths.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val build by job {
	 *     script {
	 *         shell("make build")
	 *     }
	 *
	 *     artifacts {
	 *         include("binaries/")
	 *         include(".config")
	 *         include("*.log")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactspaths)
	 */
	@GitLabCiDsl
	fun include(path: String) {
		includes += path
	}

	/**
	 * Prevents files from being added to the artifacts archive.
	 *
	 * Paths are relative to the project directory and can use wildcards with glob patterns.
	 * You can call this method multiple times to exclude multiple paths.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val build by job {
	 *     script {
	 *         shell("make build")
	 *     }
	 *
	 *     artifacts {
	 *         include("binaries/")
	 *         exclude("binaries/**/*.o")
	 *         exclude("*.tmp")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsexclude)
	 */
	@GitLabCiDsl
	fun exclude(path: String) {
		excludes += path
	}

	/**
	 * Specifies how long job artifacts are stored before they expire and are deleted.
	 *
	 * The expiration time period begins when the artifact is uploaded and stored on GitLab.
	 * If no unit is provided, the time is in seconds. Use `"never"` to prevent expiration.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val build by job {
	 *     script {
	 *         shell("make build")
	 *     }
	 *
	 *     artifacts {
	 *         include("binaries/")
	 *         expireIn("1 week")
	 *     }
	 * }
	 * ```
	 *
	 * ### Example: various time formats
	 *
	 * ```kotlin
	 * artifacts {
	 *     include("logs/")
	 *     expireIn("3 mins 4 sec")  // or "2 hrs 20 min", "6 mos 1 day", etc.
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsexpire_in)
	 */
	@GitLabCiDsl
	fun expireIn(time: String) {
		expireIn = time
	}

	/**
	 * Exposes job artifacts in the merge request UI with a custom display name.
	 *
	 * This creates a download link in the merge request UI that displays the specified name.
	 * A maximum of 10 job artifacts per merge request can be exposed.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("echo 'test results' > results.txt")
	 *     }
	 *
	 *     artifacts {
	 *         include("results.txt")
	 *         exposeAs("Test Results")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsexpose_as)
	 */
	@GitLabCiDsl
	fun exposeAs(name: String) {
		exposeAs = name
	}

	/**
	 * Defines the name of the created artifacts archive.
	 *
	 * If not defined, the default name is `artifacts`, which becomes `artifacts.zip` when downloaded.
	 * CI/CD variables are supported in the name.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val build by job {
	 *     script {
	 *         shell("make build")
	 *     }
	 *
	 *     artifacts {
	 *         include("binaries/")
	 *         name("build-artifacts-${CI_COMMIT_REF_NAME}")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsname)
	 */
	@GitLabCiDsl
	fun name(name: String) {
		this.name = name
	}

	/**
	 * Specifies when to upload artifacts based on job result.
	 *
	 * By default, artifacts are uploaded only when the job succeeds.
	 * Use this method to change when artifacts should be uploaded.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("run-tests")
	 *     }
	 *
	 *     artifacts {
	 *         include("test-results/")
	 *         rule(When.Always)  // Upload artifacts regardless of job result
	 *     }
	 * }
	 * ```
	 *
	 * ### Example: upload on failure
	 *
	 * ```kotlin
	 * artifacts {
	 *     include("logs/")
	 *     rule(When.OnFailure)  // Only upload when job fails
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactswhen)
	 */
	@GitLabCiDsl
	fun rule(rule: When) {
		this.rule = rule
	}

	//region Reports

	/**
	 * Collects accessibility test reports using pa11y to report on the accessibility impact of changes.
	 *
	 * GitLab can display the results of one or more accessibility reports in the merge request 
	 * accessibility widget, helping teams identify and fix accessibility issues.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val accessibilityTest by job {
	 *     script {
	 *         shell("pa11y-ci --sitemap http://localhost:3000/sitemap.xml --reporter json > accessibility-report.json")
	 *     }
	 *
	 *     artifacts {
	 *         accessibility("accessibility-report.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportsaccessibility)
	 * - [Accessibility testing](https://docs.gitlab.com/ee/ci/testing/accessibility_testing.html)
	 */
	@GitLabCiDsl
	fun accessibility(path: String) {
		reports[yaml("accessibility")] = yaml(path)
	}

	/**
	 * Collects annotations to attach auxiliary data to a job.
	 *
	 * The annotations report is a JSON file with annotation sections that can contain
	 * external links and other auxiliary data displayed on the job output page.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val deploy by job {
	 *     script {
	 *         shell("./deploy.sh")
	 *         shell("echo '{\"links\": [{\"external_link\": {\"label\": \"Deployment URL\", \"url\": \"https://app.example.com\"}}]}' > annotations.json")
	 *     }
	 *
	 *     artifacts {
	 *         annotations("annotations.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportsannotations)
	 */
	@GitLabCiDsl
	fun annotations(path: String) {
		reports[yaml("annotations")] = yaml(path)
	}

	/**
	 * Collects API fuzzing security test reports.
	 *
	 * GitLab can display the results of one or more API fuzzing reports in:
	 * - The merge request security widget
	 * - The Project Vulnerability report
	 * - The pipeline Security tab
	 * - The security dashboard
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val apiFuzzing by job {
	 *     script {
	 *         shell("run-api-fuzzing-tests")
	 *     }
	 *
	 *     artifacts {
	 *         apiFuzzing("gl-api-fuzzing-report.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportsapi_fuzzing)
	 */
	@GitLabCiDsl
	fun apiFuzzing(path: String) {
		reports[yaml("api_fuzzing")] = yaml(path)
	}

	/**
	 * Collects browser performance test reports.
	 *
	 * The browser performance report collects Browser Performance Testing metrics as an artifact.
	 * This artifact is a JSON file output by the Sitespeed plugin. GitLab can display the results
	 * of only one report in the merge request browser performance testing widget.
	 *
	 * GitLab cannot display the combined results of multiple browser_performance reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val performanceTest by job {
	 *     script {
	 *         shell("sitespeed.io --outputFolder sitespeed-results https://example.com")
	 *     }
	 *
	 *     artifacts {
	 *         browserPerformance("sitespeed-results/data/performance.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportsbrowser_performance)
	 */
	@GitLabCiDsl
	fun browserPerformance(path: String) {
		reports[yaml("browser_performance")] = yaml(path)
	}

	/**
	 * Collects cluster image scanning security reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     clusterImageScanning("gl-cluster-image-scanning-report.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun clusterImageScanning(path: String) {
		reports[yaml("cluster_image_scanning")] = yaml(path)
	}

	/**
	 * Collects code coverage reports in Cobertura or JaCoCo formats.
	 *
	 * The coverage report is uploaded to GitLab as an artifact. You can generate multiple
	 * JaCoCo or Cobertura reports and include them using wildcards - the results are
	 * aggregated in the final coverage report. The results appear in merge request diff annotations.
	 *
	 * Coverage reports from child pipelines appear in merge request diff annotations,
	 * but the artifacts themselves are not shared with parent pipelines.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("./gradlew test jacocoTestReport")
	 *     }
	 *
	 *     artifacts {
	 *         coverage("cobertura", "build/reports/jacoco/test/jacocoTestReport.xml")
	 *     }
	 * }
	 * ```
	 *
	 * ### Example: JaCoCo format with wildcards
	 *
	 * ```kotlin
	 * artifacts {
	 *     coverage("jacoco", "target/site/jacoco/jacoco*.xml")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportscoverage_report)
	 */
	@GitLabCiDsl
	fun coverage(format: String, path: String) {
		reports[yaml("coverage_report")] = yamlMap(
			mapOf(
				"coverage_format" to format,
				"path" to path,
			)
		)
	}

	/**
	 * Collects code quality reports to display code quality information in GitLab.
	 *
	 * The collected code quality report uploads to GitLab as an artifact with an expiration
	 * time of 1 week. GitLab can display the results of one or more reports in:
	 * - The merge request code quality widget
	 * - The merge request diff annotations
	 * - The full report
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val code-quality by job {
	 *     script {
	 *         shell("sonar-scanner -Dsonar.qualitygate.wait=true")
	 *     }
	 *
	 *     artifacts {
	 *         codeQuality("gl-code-quality-report.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportscodequality)
	 */
	@GitLabCiDsl
	fun codeQuality(path: String) {
		reports[yaml("codequality")] = yaml(path)
	}

	/**
	 * Collects container scanning security reports.
	 *
	 * The collected Container Scanning report uploads to GitLab as an artifact.
	 * GitLab can display the results of one or more reports in:
	 * - The merge request container scanning widget
	 * - The pipeline Security tab
	 * - The security dashboard
	 * - The Project Vulnerability report
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val containerScan by job {
	 *     script {
	 *         shell("docker run --rm -v /var/run/docker.sock:/var/run/docker.sock registry.gitlab.com/security-products/container-scanning")
	 *     }
	 *
	 *     artifacts {
	 *         containerScanning("gl-container-scanning-report.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportscontainer_scanning)
	 */
	@GitLabCiDsl
	fun containerScanning(path: String) {
		reports[yaml("container_scanning")] = yaml(path)
	}

	/**
	 * Collects coverage fuzzing security test reports.
	 *
	 * The collected coverage fuzzing report uploads to GitLab as an artifact.
	 * GitLab can display the results of one or more reports in:
	 * - The merge request coverage fuzzing widget
	 * - The pipeline Security tab
	 * - The Project Vulnerability report
	 * - The security dashboard
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val coverageFuzzing by job {
	 *     script {
	 *         shell("run-coverage-fuzzing-tests")
	 *     }
	 *
	 *     artifacts {
	 *         coverageFuzzing("gl-coverage-fuzzing-report.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportscoverage_fuzzing)
	 */
	@GitLabCiDsl
	fun coverageFuzzing(path: String) {
		reports[yaml("coverage_fuzzing")] = yaml(path)
	}

	/**
	 * Collects CycloneDX software bill of materials (SBOM) reports.
	 *
	 * This report is a Software Bill of Materials describing the components of a project
	 * following the CycloneDX protocol format. You can specify multiple CycloneDX reports
	 * using filename patterns, arrays of filenames, or both. Directories are not supported.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val sbomGeneration by job {
	 *     script {
	 *         shell("cyclonedx-npm --output-file gl-sbom-npm.cdx.json")
	 *         shell("cyclonedx-bundler --output-file gl-sbom-bundler.cdx.json")
	 *     }
	 *
	 *     artifacts {
	 *         cyclonedx("gl-sbom-npm.cdx.json", "gl-sbom-bundler.cdx.json")
	 *     }
	 * }
	 * ```
	 *
	 * ### Example: using filename patterns
	 *
	 * ```kotlin
	 * artifacts {
	 *     cyclonedx("gl-sbom-*.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreportscyclonedx)
	 */
	@GitLabCiDsl
	fun cyclonedx(vararg path: String) {
		reports[yaml("cyclonedx")] = yamlList(path.asList())
	}

	/**
	 * Collects Dynamic Application Security Testing (DAST) reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     dast("gl-dast-report.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun dast(path: String) {
		reports[yaml("dast")] = yaml(path)
	}

	/**
	 * Collects dependency scanning security reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     dependencyScanning("gl-dependency-scanning-report.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun dependencyScanning(path: String) {
		reports[yaml("dependency_scanning")] = yaml(path)
	}

	/**
	 * Collects environment variables from a dotenv file to pass to downstream jobs.
	 *
	 * The dotenv file should contain key-value pairs in the format `KEY=value`.
	 * These variables become available as CI/CD variables in downstream jobs.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val build by job {
	 *     script {
	 *         shell("echo 'BUILD_VERSION=1.2.3' >> build.env")
	 *         shell("echo 'BUILD_URL=https://example.com' >> build.env")
	 *     }
	 *
	 *     artifacts {
	 *         dotenv("build.env")
	 *     }
	 * }
	 *
	 * val deploy by job {
	 *     script {
	 *         shell("echo 'Deploying version: $BUILD_VERSION'")
	 *         shell("echo 'Build URL: $BUILD_URL'")
	 *     }
	 *     needs(build)
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun dotenv(path: String) {
		reports[yaml("dotenv")] = yaml(path)
	}

	/**
	 * Collects JUnit test reports to display test results in GitLab's UI.
	 *
	 * The JUnit report provides detailed information about test execution, including
	 * passed, failed, and skipped tests. GitLab displays this information in merge requests
	 * and pipeline views.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("./gradlew test")
	 *     }
	 *
	 *     artifacts {
	 *         junit("build/test-results/test/TEST-*.xml")
	 *     }
	 * }
	 * ```
	 *
	 * ### Example: Maven project
	 *
	 * ```kotlin
	 * val test by job {
	 *     script {
	 *         shell("mvn test")
	 *     }
	 *
	 *     artifacts {
	 *         junit("target/surefire-reports/TEST-*.xml")
	 *     }
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun junit(path: String) {
		reports[yaml("junit")] = yaml(path)
	}

	/**
	 * Collects license scanning reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     licenseScanning("gl-license-scanning-report.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun licenseScanning(path: String) {
		reports[yaml("license_scanning")] = yaml(path)
	}

	/**
	 * Collects load performance test reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     loadPerformance("performance.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun loadPerformance(path: String) {
		reports[yaml("load_performance")] = yaml(path)
	}

	/**
	 * Collects metrics reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     metrics("metrics.txt")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun metrics(path: String) {
		reports[yaml("metric")] = yaml(path)
	}

	/**
	 * Collects requirements reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     requirements("requirements.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun requirements(path: String) {
		reports[yaml("requirements")] = yaml(path)
	}

	/**
	 * Collects Static Application Security Testing (SAST) reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     sast("gl-sast-report.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun sast(path: String) {
		reports[yaml("sast")] = yaml(path)
	}

	/**
	 * Collects secret detection security reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     secretDetection("gl-secret-detection-report.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun secretDetection(path: String) {
		reports[yaml("secret_detection")] = yaml(path)
	}

	/**
	 * Collects Terraform plan reports.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * artifacts {
	 *     terraform("tfplan.json")
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#artifactsreports)
	 */
	@GitLabCiDsl
	fun terraform(path: String) {
		reports[yaml("terraform")] = yaml(path)
	}

	//endregion

	override fun toYaml(): Yaml = yamlMap {
		add("paths", includes)
		add("exclude", excludes)
		addNotNull("expire_in", expireIn)
		addNotNull("expose_as", exposeAs)
		addNotNull("name", name)
		add("reports", reports)
		add("when", rule)
	}

}
