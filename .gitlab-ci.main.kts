#!/usr/bin/env kotlin

/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

@file:DependsOn("dev.opensavvy.gitlab:gitlab-ci-kotlin-jvm:0.3.1")

import opensavvy.gitlab.ci.*
import opensavvy.gitlab.ci.script.*
import opensavvy.gitlab.ci.plugins.*
import opensavvy.gitlab.ci.plugins.Gradle.Companion.gradlew
import opensavvy.gitlab.ci.plugins.Gradle.Companion.useGradle

val pipeline = gitlabCi {
	val test by stage()

	val helloWorld by job(stage = test) {
		script {
			shell("echo 'Hello world'")
		}
	}

	val build by job {
		useGradle()

		script {
			gradlew.task("build")
		}
	}
}

pipeline.println()
