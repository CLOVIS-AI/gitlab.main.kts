#!/usr/bin/env kotlin

@file:Repository("https://gitlab.com/api/v4/projects/33995298/packages/maven")
@file:DependsOn("opensavvy:gitlab-ci.kt:0.1.0-post.18+457b36dcec")
@file:Suppress("UNUSED_VARIABLE")

import opensavvy.gitlab.ci.gitlabCi
import opensavvy.gitlab.ci.job
import opensavvy.gitlab.ci.script.shell
import opensavvy.gitlab.ci.stage

val pipeline = gitlabCi {
	val test by stage()

	val helloWorld by job(stage = test) {
		script {
			shell("echo 'Hello world'")
		}
	}
}

pipeline.println()
