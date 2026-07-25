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

import opensavvy.gitlab.ci.diff.assertEqualsFile
import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradlew
import opensavvy.gitlab.ci.script.shell
import opensavvy.prepared.runner.testballoon.preparedSuite

val BasicTest by preparedSuite {

	test("Test retry functionality") {
		val pipeline = gitlabCi {
			val build by stage()

			// Simple retry
			val simpleRetry by job(stage = build) {
				script {
					shell("echo 'Simple retry test'")
				}
				retry(2)
			}

			// Retry with when type
			val retryWithWhen by job(stage = build) {
				script {
					shell("echo 'Retry with when type test'")
				}
				retry(2) {
					on(RetryCause.RunnerSystemFailure)
				}
			}

			// Retry with multiple when types
			val retryWithMultipleWhen by job(stage = build) {
				script {
					shell("echo 'Retry with multiple when types test'")
				}
				retry(2) {
					on(RetryCause.RunnerSystemFailure)
					on(RetryCause.ApiFailure)
				}
			}

			// Retry with exit code
			val retryWithExitCode by job(stage = build) {
				script {
					shell("echo 'Retry with exit code test'")
				}
				retry(2) {
					onExitCode(137)
				}
			}

			// Retry with multiple exit codes
			val retryWithMultipleExitCodes by job(stage = build) {
				script {
					shell("echo 'Retry with multiple exit codes test'")
				}
				retry(2) {
					onExitCode(137)
					onExitCode(255)
				}
			}

			// Retry with both when type and exit code
			val retryWithBoth by job(stage = build) {
				script {
					shell("echo 'Retry with both when type and exit code test'")
				}
				retry(2) {
					on(RetryCause.RunnerSystemFailure)
					onExitCode(137)
				}
			}
		}

		// Verify the YAML output contains the expected retry configurations
		val yaml = pipeline.toYaml().toYamlString()

		assertEqualsFile("retry.gitlab-ci.yml", yaml)
	}

	test("Test allowFailure functionality") {
		val pipeline = gitlabCi {
			val test by stage()

			// Simple allowFailure true
			job("allowFailureTrue", stage = test) {
				script { shell("exit 1") }
				allowFailure(true)
			}

			// Simple allowFailure false
			job("allowFailureFalse", stage = test) {
				script { shell("exit 1") }
				allowFailure(false)
			}

			// allowFailure with single exit code
			job("allowFailureExitCode", stage = test) {
				script { shell("exit 137") }
				allowFailure {
					onExitCode(137)
				}
			}

			// allowFailure with multiple exit codes
			job("allowFailureMultipleExitCodes", stage = test) {
				script { shell("exit 255") }
				allowFailure {
					onExitCode(137)
					onExitCode(255)
				}
			}
		}

		val yaml = pipeline.toYaml().toYamlString()
		assertEqualsFile("allow-failure.gitlab-ci.yml", yaml)
	}

	test("Test afterScript functionality") {
		val pipeline = gitlabCi {
			val test by stage()

			// Single command in afterScript
			job("afterScriptSingle", stage = test) {
				script { shell("echo 'main script'") }
				afterScript {
					shell("echo 'cleanup'")
				}
			}

			// Multiple commands in afterScript
			job("afterScriptMultiple", stage = test) {
				script { shell("echo 'main script'") }
				afterScript {
					shell("echo 'first cleanup step'")
					shell("echo 'second cleanup step'")
				}
			}

			// afterScript combined with beforeScript and script
			job("afterScriptWithBeforeScript", stage = test) {
				beforeScript {
					shell("echo 'setup'")
				}
				script {
					shell("echo 'main script'")
				}
				afterScript {
					shell("echo 'cleanup'")
				}
			}
		}

		val yaml = pipeline.toYaml().toYamlString()
		assertEqualsFile("after-script.gitlab-ci.yml", yaml)
	}

	test("Test artifacts functionality") {
		val pipeline = gitlabCi {
			val test by stage()

			// paths and exclude
			job("artifactsPathsAndExclude", stage = test) {
				script { shell("make build") }
				artifacts {
					include("binaries/")
					include(".config")
					exclude("binaries/**/*.o")
				}
			}

			// expire_in, expose_as and name
			job("artifactsMetadata", stage = test) {
				script { shell("echo 'test' > file.txt") }
				artifacts {
					include("file.txt")
					expireIn("1 week")
					exposeAs("artifact 1")
					name("job1-artifacts-file")
				}
			}

			// untracked
			job("artifactsUntracked", stage = test) {
				script { shell("make build") }
				artifacts {
					includeUntracked(true)
				}
			}

			// access
			job("artifactsAccess", stage = test) {
				script { shell("make build") }
				artifacts {
					access(AccessLevel.Developer)
				}
			}

			// public (deprecated, superseded by access)
			job("artifactsPublic", stage = test) {
				script { shell("make build") }
				artifacts {
					public(false)
				}
			}

			// when
			job("artifactsWhenOnFailure", stage = test) {
				script { shell("run-tests") }
				artifacts {
					include("logs/")
					rule(When.OnFailure)
				}
			}

			// reports
			job("artifactsReports", stage = test) {
				script { shell("./gradlew test") }
				artifacts {
					junit("build/test-results/test/TEST-*.xml")
				}
			}
		}

		val yaml = pipeline.toYaml().toYamlString()
		assertEqualsFile("artifacts.gitlab-ci.yml", yaml)
	}

	test("Test cache functionality") {
		val pipeline = gitlabCi {
			val test by stage()

			// paths and simple key
			job("cachePathsAndKey", stage = test) {
				script { shell("echo 'This job uses a cache.'") }
				cache {
					key("binaries-cache")
					include("binaries/*.apk")
					include(".config")
				}
			}

			// key:files
			job("cacheKeyFiles", stage = test) {
				script { shell("echo 'This job uses a cache.'") }
				cache {
					keyFile("Gemfile.lock")
					keyFile("package.json")
					include("vendor/ruby")
					include("node_modules")
				}
			}

			// key:files_commits
			job("cacheKeyFilesCommits", stage = test) {
				script { shell("echo 'This job uses a commit-based cache.'") }
				cache {
					keyFileCommit("package.json")
					keyFileCommit("yarn.lock")
					include("node_modules")
				}
			}

			// key:prefix
			job("cacheKeyPrefix", stage = test) {
				script { shell("echo 'This rspec job uses a cache.'") }
				cache {
					keyFile("Gemfile.lock")
					keyPrefix("rspec")
					include("vendor/ruby")
				}
			}

			// untracked
			job("cacheUntracked", stage = test) {
				script { shell("test") }
				cache {
					untracked(true)
					include("binaries/")
				}
			}

			// unprotect
			job("cacheUnprotect", stage = test) {
				script { shell("test") }
				cache {
					unprotect(true)
				}
			}

			// when
			job("cacheWhenAlways", stage = test) {
				script { shell("rspec") }
				cache {
					include("rspec/")
					rule(When.Always)
				}
			}

			// policy
			job("cachePolicyPush", stage = test) {
				script { shell("This job only downloads dependencies and builds the cache.") }
				cache {
					key("gems")
					include("vendor/bundle")
					policy(CachePolicy.Push)
				}
			}

			job("cachePolicyPull", stage = test) {
				script { shell("This job script uses the cache, but does not update it.") }
				cache {
					key("gems")
					include("vendor/bundle")
					policy(CachePolicy.Pull)
				}
			}

			// fallback_keys
			job("cacheFallbackKeys", stage = test) {
				script { shell("rspec") }
				cache {
					key("gems-branch")
					include("rspec/")
					fallbackKey("gems")
					rule(When.Always)
				}
			}
		}

		val yaml = pipeline.toYaml().toYamlString()
		assertEqualsFile("cache.gitlab-ci.yml", yaml)
	}


	test("Test dast_configuration functionality") {
		val pipeline = gitlabCi {
			val dast by stage()

			job("dast", stage = dast) {
				script { shell("echo 'dast'") }
				dastConfiguration {
					siteProfile("Example Co")
					scannerProfile("Quick Passive Test")
				}
			}
		}

		val yaml = pipeline.toYaml().toYamlString()
		assertEqualsFile("dast-configuration.gitlab-ci.yml", yaml)
	}
	test("Generate a basic CI inspired by Pedestal") {
		val pipeline = gitlabCi {
			val build by stage()
			val test by stage()
			val publish by stage()

			val modules = listOf("logger", "backbone")
			fun Job.publish(module: String, publication: String, repository: String) {
				image("archlinux")

				script {
					shell("pacman -Syu --noconfirm git jre-openjdk-headless")
					gradlew.task("$module:publish${publication}PublicationTo${repository}Repository")
				}

				interruptible(false)
			}

			if (Value.isDefaultBranch || Value.isTag) {
				for (module in modules) {
					job("$module:publish", stage = publish) {
						publish(module, "KotlinMultiplatform", "GitLab")
					}
				}
			}

			val dokka by job(stage = build) {
				script {
					gradlew.task("dokkaHtmlMultiModule")
					shell("mv build/dokka/htmlMultiModule documentation")
				}

				artifacts {
					name("Documentation")
					exposeAs("Documentation")

					include("documentation")
				}

				interruptible(true)
			}

			if (Value.isDefaultBranch) {
				val pages by job(stage = publish) {
					dependsOn(dokka, artifacts = true)

					script {
						shell("mkdir -p public")
						shell("mv documentation public")
					}

					artifacts {
						include("public")
					}

					interruptible(false)
				}
			}
		}

		assertEqualsFile("basic.gitlab-ci.yaml", pipeline)
	}

}
