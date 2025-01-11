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
import opensavvy.gitlab.ci.plugins.Docker.Companion.docker
import opensavvy.gitlab.ci.plugins.Docker.Companion.useContainerRegistry
import opensavvy.gitlab.ci.plugins.Docker.Companion.useDockerInDocker
import opensavvy.gitlab.ci.plugins.Helm.Companion.helm
import opensavvy.gitlab.ci.script.shell
import opensavvy.prepared.runner.kotest.PreparedSpec

// Inspired by https://gitlab.com/opensavvy/minecraft-server/-/blob/main/.gitlab-ci.yml

class MinecraftTest : PreparedSpec({

	test("Generate a simple CI file inspired by our Minecraft server") {
		val ci = gitlabCi {
			val dockerStage = stage("docker")
			val deploy by stage()

			val k8sImage = "${Variable.Registry.image}/build/k8s"
			val tmpVersion = Variable.MergeRequest.iid

			val buildK8s = job("k8s:docker:build", stage = dockerStage) {
				useDockerInDocker()
				useContainerRegistry()

				script {
					docker.build(k8sImage, dockerfile = "k8s.dockerfile")
					docker.push(k8sImage)
				}
			}

			job("k8s:docker:latest", stage = deploy) {
				useDockerInDocker()
				useContainerRegistry()

				dependsOn(buildK8s)

				script {
					docker.rename(k8sImage)
				}
			}

			if (Value.isDefaultBranch) {
				job("deploy", stage = deploy) {
					dependsOn(buildK8s)

					image(k8sImage)

					beforeScript {
						shell("kubectl config use-context opensavvy/config:opensavvy-agent")
						helm.addRepository("itzg", "https://itzg.github.io/minecraft-server-charts")
						helm.updateRepositories()
					}

					script {
						helm.deploy(
							release = "minecraft",
							chart = "itzg/minecraft",
							values = listOf("minecraft.yml"),
							namespace = "minecraft"
						)
					}
				}
			}
		}

		assertEqualsFile("minecraft.gitlab-ci.yml", ci)
	}

})
