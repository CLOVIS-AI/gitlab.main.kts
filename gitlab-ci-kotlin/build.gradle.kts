@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
}

kotlin {
	jvm()

	sourceSets.commonTest.dependencies {
		implementation(libs.prepared)
		implementation(libs.prepared.files)
	}
}

library {
	name.set(".gitlab-ci.kt")
	description.set("Write your GitLab CI pipelines in Kotlin")
	homeUrl.set("https://gitlab.com/opensavvy/automation/gitlab-ci.kt")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
