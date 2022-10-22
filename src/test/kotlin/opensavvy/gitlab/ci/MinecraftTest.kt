package opensavvy.gitlab.ci

import opensavvy.gitlab.ci.plugins.Docker.Companion.docker
import opensavvy.gitlab.ci.plugins.Docker.Companion.useContainerRegistry
import opensavvy.gitlab.ci.plugins.Docker.Companion.useDockerInDocker
import opensavvy.gitlab.ci.plugins.Helm.Companion.helm
import opensavvy.gitlab.ci.script.shell
import kotlin.test.Test

// Inspired by https://gitlab.com/opensavvy/minecraft-server/-/blob/main/.gitlab-ci.yml

class MinecraftTest {

	@Test
	fun minecraft() {
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

		ci.println()
	}
}
