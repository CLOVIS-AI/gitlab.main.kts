/*
 * Copyright (c) 2022, OpenSavvy and contributors.
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

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libsCommon.plugins.testBalloon)
}

kotlin {
	jvm()

	sourceSets.commonTest.dependencies {
		implementation(libsCommon.opensavvy.prepared.testBalloon)
		implementation(libsCommon.opensavvy.prepared.filesystem)
	}

	compilerOptions {
		freeCompilerArgs.set(listOf("-Xmulti-dollar-interpolation"))
	}
}

library {
	name.set("GitLab CI Kotlin DSL")
	description.set("Write your GitLab CI pipelines in Kotlin")
	homeUrl.set("https://opensavvy.gitlab.io/automation/gitlab-ci.kt/docs/")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

val overrideTestResults: String? by project

tasks.withType<Test> {
	environment(
		"CI_COMMIT_BRANCH" to "main",
		"CI_DEFAULT_BRANCH" to "main",
		"CI_COMMIT_TAG" to "2.0",
		"CI_REGISTRY_IMAGE" to "https://registry.gitlab.com/group/project",
	)

	if (overrideTestResults != null) {
		environment("tests_override_results", "true")
	}
}
