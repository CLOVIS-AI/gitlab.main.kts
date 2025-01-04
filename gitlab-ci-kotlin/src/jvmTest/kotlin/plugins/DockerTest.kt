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

package opensavvy.gitlab.ci.plugins

import opensavvy.gitlab.ci.Value
import opensavvy.gitlab.ci.Variable
import opensavvy.gitlab.ci.diff.assertEqualsFile
import opensavvy.gitlab.ci.gitlabCi
import opensavvy.gitlab.ci.job
import opensavvy.gitlab.ci.plugins.Docker.Companion.docker
import opensavvy.gitlab.ci.plugins.Docker.Companion.useContainerRegistry
import opensavvy.gitlab.ci.plugins.Docker.Companion.useDockerInDocker
import opensavvy.prepared.runner.kotest.PreparedSpec

class DockerTest : PreparedSpec({

	test("Build image") {
		/*
		 * This example demonstrates how to:
		 * - Build a Dockerfile into an image on each new commit, reusing the cache from previous builds
		 * - Publish the image to the GitLab Container Registry integrated with the current project, for the default
		 *   branch and all tags.
		 *
		 * This example assumes that:
		 * - The dockerfile is in the root of the project
		 * - The dockerfile should be built from the root of the project
		 * - We do not care about the version of Docker to use (the default is hard-coded to ensure reproducibility)
		 *
		 * All of these assumptions are encoded as the default value of the arguments passed to the various functions.
		 * If your use case is different, you can override them
		 */

		val pipeline = gitlabCi {
			// Name of a Docker image that will be pushed to the GitLab Container registry of the current project
			val backend = "${Variable.Registry.image}/backend"

			val dockerBuild by job {
				// Log in to the GitLab Container Registry for the current project
				useContainerRegistry()

				// Set the job's image and includes the DinD service
				useDockerInDocker()

				script {
					// Builds './Dockerfile' into an image called "<registry-url>/backend:build-<pipeline-id>"
					// The image will automatically try to reuse the cache from the previous build on the default branch
					docker.build(backend)
					// Pushes the image we just built to the GitLab Container Registry for the current project
					docker.push(backend)
				}
			}

			// If we're running on the default branch or on a tag, we want to appropriately rename the image
			if (Value.isTag || Value.isDefaultBranch) {
				job("docker-publish") {
					// Don't start until the build is over
					dependsOn(dockerBuild)

					useContainerRegistry()
					useDockerInDocker()

					script {
						// Rename the built image with the tag's name (if we're running on a tag),
						// or "latest" if we're running on the default branch.
						docker.rename(backend, newVersion = Value.Commit.tag ?: "latest")
					}
				}
			}
		}

		assertEqualsFile("plugins/docker/buildImage.yaml", pipeline)
	}

})
