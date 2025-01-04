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

@file:Suppress("unused")

package opensavvy.gitlab.ci

/**
 * Predefined variables in GitLab CI.
 *
 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html).
 */
@Suppress("unused")
object Variable {

	/**
	 * Variables related to [ChatOps](https://docs.gitlab.com/ee/ci/chatops/index.html).
	 */
	object Chat {
		/**
		 * Source chat channel that triggered this command.
		 */
		const val channel = "\$CHAT_CHANNEL"

		/**
		 * Additional arguments passed to this command.
		 */
		const val input = "\$CHAT_INPUT"

		/**
		 * User ID of the person who started this command.
		 */
		const val userId = "\$CHAT_USER_ID"
	}

	/**
	 * `true` for all jobs executed in CI.
	 */
	const val active = "\$CI"

	/**
	 * The GitLab API v4 URL.
	 */
	const val apiV4URL = "\$CI_API_V4_URL"

	/**
	 * Top-level directory in which builds are executed.
	 */
	const val buildDir = "\$CI_BUILDS_DIR"

	/**
	 * The unique ID of build execution in a single executor.
	 */
	const val concurrentId = "\$CI_CONCURRENT_ID"

	/**
	 * The unique ID of build execution in a single executor and project.
	 */
	const val concurrentProjectId = "\$CI_CONCURRENT_PROJECT_ID"

	/**
	 * The path to the CI/CD configuration file.
	 *
	 * Defaults to `.gitlab-ci.yml`.
	 */
	const val configPath = "\$CI_CONFIG_PATH"

	/**
	 * `true` if debug logging is enabled.
	 *
	 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/variables/index.html#debug-logging).
	 */
	const val debugTrace = "\$CI_DEBUG_TRACE"

	/**
	 * The project's default branch.
	 */
	const val defaultBranch = "\$CI_DEFAULT_BRANCH"

	/**
	 * Variables related to commits.
	 */
	object Commit {
		/**
		 * Author of the commit, in the `Name <email>` format.
		 */
		const val author = "\$CI_COMMIT_AUTHOR"

		/**
		 * Previous latest commit present on a branch.
		 * Is always `0000000000000000000000000000000000000000` in merge request pipelines.
		 */
		const val previousCommitSha = "\$CI_COMMIT_BEFORE_SHA"

		/**
		 * The commit branch name.
		 *
		 * Available in branch pipelines, including pipelines for the default branch.
		 * Not available in merge request pipelines or tag pipelines.
		 */
		const val branch = "\$CI_COMMIT_BRANCH"

		/**
		 * The commit tag name.
		 *
		 * Available only in pipeline for tags.
		 */
		const val tag = "\$CI_COMMIT_TAG"

		/**
		 * The full commit message.
		 */
		const val message = "\$CI_COMMIT_MESSAGE"

		/**
		 * The first line of the commit [message].
		 */
		const val title = "\$CI_COMMIT_TITLE"

		/**
		 * The commit [message] without its first line, if it is shorter than 100 characters.
		 *
		 * Otherwise, same as [message].
		 */
		const val description = "\$CI_COMMIT_DESCRIPTION"

		/**
		 * The SHA of the current commit.
		 */
		const val sha = "\$CI_COMMIT_SHA"

		/**
		 * The first eight characters of the [sha].
		 */
		const val shortSha = "\$CI_COMMIT_SHORT_SHA"

		/**
		 * The timestamp of the commit in the ISO 8601 format.
		 */
		const val timestamp = "\$CI_COMMIT_TIMESTAMP"

		/**
		 * Variables that relate to the reference this pipeline is building (e.g. a tag or a branch).
		 */
		object Ref {
			/**
			 * The tag or branch name.
			 */
			const val name = "\$CI_COMMIT_REF_NAME"

			/**
			 * `true` if this reference is protected.
			 */
			const val protected = "\$CI_COMMIT_REF_PROTECTED"

			/**
			 * [name] modified to fit in host names or URLs.
			 */
			const val slug = "\$CI_COMMIT_REF_SLUG"
		}
	}

	/**
	 * The container dependency proxy allows to cache containers in GitLab to avoid Docker Hub's rate limits.
	 */
	object DependencyProxy {

		/**
		 * Top-level group image prefix for pulling images.
		 */
		const val groupImagePrefix = "\$CI_DEPENDENCY_PROXY_GROUP_IMAGE_PREFIX"

		/**
		 * The direct group image prefix for pulling images.
		 */
		const val directGroupImagePrefix = "\$CI_DEPENDENCY_PROXY_DIRECT_GROUP_IMAGE_PREFIX"

		const val username = "\$CI_DEPENDENCY_PROXY_USER"

		const val password = "\$CI_DEPENDENCY_PROXY_PASSWORD"

		const val server = "\$CI_DEPENDENCY_PROXY_SERVER"

	}

	/**
	 * Only available if the pipeline runs during a deploy freeze window.
	 *
	 * `true` when available.
	 */
	const val deployFreeze = "\$CI_DEPLOY_FREEZE"

	/**
	 * Only available if the job is executed in a disposable environment (something that is created only for this job
	 * and will be destroyed after the execution, all executors except `shell` and `ssh`).
	 *
	 * `true` when available.
	 */
	const val disposable = "\$CI_DISPOSABLE_ENVIRONMENT"

	const val shared = "\$CI_SHARED_ENVIRONMENT"

	object Environment {

		/**
		 * The name of the environment for this job.
		 */
		const val name = "\$CI_ENVIRONMENT_NAME"

		/**
		 * Simplified [name] suitable for DNS, URLs, etc.
		 */
		const val slug = "\$CI_ENVIRONMENT_SLUG"

		/**
		 * URL of the environment.
		 */
		const val url = "\$CI_ENVIRONMENT_URL"

		/**
		 * The action of this environment.
		 *
		 * Can be `start`, `prepare` or `stop`.
		 */
		const val action = "\$CI_ENVIRONMENT_ACTION"

		/**
		 * The deployment tier for this job.
		 */
		const val tier = "\$CI_ENVIRONMENT_TIER"
	}

	/**
	 * Whether FIPS mode is enabled in the GitLab instance.
	 */
	const val fipsMode = "\$CI_GITLAB_FIPS_MODE"

	/**
	 * Only available if the pipeline's project has an open requirement.
	 *
	 * `true` when available.
	 */
	const val hasOpenRequirements = "\$CI_HAS_OPEN_REQUIREMENTS"

	object Job {
		const val id = "\$CI_JOB_ID"
		const val image = "\$CI_JOB_IMAGE"
		const val jwt = "\$CI_JOB_JWT"
		const val manual = "\$CI_JOB_MANUAL"
		const val name = "\$CI_JOB_MANUAL"
		const val stage = "\$CI_JOB_STAGE"
		const val status = "\$CI_JOB_STATUS"
		const val token = "\$CI_JOB_TOKEN"
		const val url = "\$CI_JOB_URL"
		const val startedAt = "\$CI_JOB_STARTED_AT"
	}

	const val kubernetesActive = "\$CI_KUBERNETES_ACTIVE"

	object Parallel {
		const val index = "\$CI_NODE_INDEX"
		const val total = "\$CI_NODE_TOTAL"
	}

	const val openMergeRequests = "\$CI_OPEN_MERGE_REQUESTS"

	object Pages {
		const val domain = "\$CI_PAGES_DOMAIN"
		const val url = "\$CI_PAGES_URL"
	}

	object Pipeline {
		const val id = "\$CI_PIPELINE_ID"
		const val iid = "\$CI_PIPELINE_IID"
		const val source = "\$CI_PIPELINE_SOURCE"
		const val triggered = "\$CI_PIPELINE_TRIGGERED"
		const val url = "\$CI_PIPELINE_URL"
		const val createdAt = "\$CI_PIPELINE_CREATED_AT"
	}

	object Project {
		const val directory = "\$CI_PROJECT_DIR"
		const val id = "\$CI_PROJECT_ID"
		const val name = "\$CI_PROJECT_NAME"
		const val namespace = "\$CI_PROJECT_NAMESPACE"
		const val path = "\$CI_PROJECT_PATH"
		const val pathSlug = "\$CI_PROJECT_PATH_SLUG"
		const val languages = "\$CI_PROJECT_REPOSITORY_LANGUAGES"
		const val rootNamespace = "\$CI_PROJECT_ROOT_NAMESPACE"
		const val title = "\$CI_PROJECT_TITLE"
		const val url = "\$CI_PROJECT_URL"
		const val visibility = "\$CI_PROJECT_VISIBILITY"
		const val classificationLabel = "\$CI_PROJECT_CLASSIFICATION_LABEL"
		const val cloneUrl = "\$CI_REPOSITORY_URL"
	}

	object Registry {
		const val image = "\$CI_REGISTRY_IMAGE"
		const val server = "\$CI_REGISTRY"
		const val username = "\$CI_REGISTRY_USER"
		const val password = "\$CI_REGISTRY_PASSWORD"
	}

	object Runner {
		const val description = "\$CI_RUNNER_DESCRIPTION"
		const val arch = "\$ĈI_RUNNER_EXECUTABLE_ARCH"
		const val id = "\$CI_RUNNER_ID"
		const val revision = "\$CI_RUNNER_REVISION"
		const val shortToken = "\$CI_RUNNER_SHORT_TOKEN"
		const val tags = "\$CI_RUNNER_TAGS"
		const val version = "\$CI_RUNNER_VERSION"
	}

	object Server {
		const val host = "\$CI_SERVER_HOST"
		const val name = "\$CI_SERVER_NAME"
		const val port = "\$CI_SERVER_PORT"
		const val protocol = "\$CI_SERVER_PROTOCOL"
		const val revision = "\$CI_SERVER_REVISION"
		const val url = "\$CI_SERVER_URL"

		object Version {
			const val major = "\$CI_SERVER_VERSION_MAJOR"
			const val minor = "\$CI_SERVER_VERSION_MINOR"
			const val patch = "\$CI_SERVER_VERSION_PATCH"
			const val full = "\$CI_SERVER_VERSION"
		}
	}

	const val features = "\$GITLAB_FEATURES"

	object User {
		const val id = "\$GITLAB_USER_ID"
		const val email = "\$GITLAB_USER_EMAIL"
		const val login = "\$GITLAB_USER_LOGIN"
		const val name = "\$GITLAB_USER_NAME"
	}

	const val triggerPayload = "\$TRIGGER_PAYLOAD"

	object MergeRequest {
		const val approved = "\$CI_MERGE_REQUEST_APPROVED"
		const val assignees = "\$CI_MERGE_REQUEST_ASSIGNEES"
		const val id = "\$CI_MERGE_REQUEST_ID"
		const val iid = "\$CI_MERGE_REQUEST_IID"
		const val labels = "\$CI_MERGE_REQUEST_LABELS"
		const val milestone = "\$CI_MERGE_REQUEST_MILESTONE"
		const val refPath = "\$CI_MERGE_REQUEST_REF_PATH"
		const val title = "\$CI_MERGE_REQUEST_TITLE"
		const val eventType = "\$CI_MERGE_REQUEST_EVENT_TYPE"
		const val diffId = "\$CI_MERGE_REQUEST_DIFF_ID"
		const val diffBaseSha = "\$CI_MERGE_REQUEST_DIFF_BASE_SHA"

		object Target {
			const val branch = "\$CI_MERGE_REQUEST_TARGET_BRANCH_NAME"
			const val branchSha = "\$CI_MERGE_REQUEST_TARGET_BRANCH_SHA"
			const val projectId = "\$CI_MERGE_REQUEST_PROJECT_ID"
			const val projectPath = "\$CI_MERGE_REQUEST_PROJECT_PATH"
			const val projectUrl = "\$CI_MERGE_REQUEST_PROJECT_URL"
		}

		object Source {
			const val branch = "\$CI_MERGE_REQUEST_SOURCE_BRANCH_NAME"
			const val branchSha = "\$CI_MERGE_REQUEST_SOURCE_BRANCH_SHA"
			const val projectId = "\$CI_MERGE_REQUEST_SOURCE_PROJECT_ID"
			const val projectPath = "\$CI_MERGE_REQUEST_SOURCE_PROJECT_PATH"
			const val projectUrl = "\$CI_MERGE_REQUEST_SOURCE_PROJECT_URL"
		}
	}

	object ExternalPullRequest {
		const val iid = "\$CI_EXTERNAL_PULL_REQUEST_IID"

		object Target {
			const val repository = "\$CI_EXTERNAL_PULL_REQUEST_TARGET_REPOSITORY"
			const val branch = "\$CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_NAME"
			const val branchSha = "\$CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_SHA"
		}

		object Source {
			const val repository = "\$CI_EXTERNAL_PULL_REQUEST_SOURCE_REPOSITORY"
			const val branch = "\$CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_NAME"
			const val branchSha = "\$CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_SHA"
		}
	}
}

object Value {

	private fun env(name: String): String? = System.getenv(name.removePrefix("\$"))

	/**
	 * `true` for all jobs executed in CI.
	 */
	val active get() = env(Variable.active)?.toBoolean()

	val apiV4URL get() = env(Variable.apiV4URL)

	/**
	 * Top-level directory in which builds are executed.
	 */
	val buildDir get() = env(Variable.buildDir)

	/**
	 * The unique ID of build execution in a single executor.
	 */
	val concurrentId get() = env(Variable.concurrentId)

	/**
	 * The unique ID of build execution in a single executor and project.
	 */
	val concurrentProjectId get() = env(Variable.concurrentProjectId)

	/**
	 * The path to the CI/CD configuration file.
	 *
	 * Defaults to `.gitlab-ci.yml`.
	 */
	val configPath get() = env(Variable.configPath)

	/**
	 * `true` if debug logging is enabled.
	 *
	 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/variables/index.html#debug-logging).
	 */
	val debugTrace get() = env(Variable.debugTrace)

	/**
	 * The project's default branch.
	 */
	val defaultBranch get() = env(Variable.defaultBranch) ?: error("No default branch")

	val isDefaultBranch get() = defaultBranch == Commit.branch
	val isTag get() = Commit.tag != null

	/**
	 * Variables related to commits.
	 */
	object Commit {

		/**
		 * Author of the commit, in the `Name <email>` format.
		 */
		val author get() = env(Variable.Commit.author)

		/**
		 * Previous latest commit present on a branch.
		 * Is always `0000000000000000000000000000000000000000` in merge request pipelines.
		 */
		val previousCommitSha get() = env(Variable.Commit.previousCommitSha)

		/**
		 * The commit branch name.
		 *
		 * Available in branch pipelines, including pipelines for the default branch.
		 * Not available in merge request pipelines or tag pipelines.
		 */
		val branch get() = env(Variable.Commit.branch)

		/**
		 * The commit tag name.
		 *
		 * Available only in pipeline for tags.
		 */
		val tag get() = env(Variable.Commit.tag)

		/**
		 * The full commit message.
		 */
		val message get() = env(Variable.Commit.message)

		/**
		 * The first line of the commit [message].
		 */
		val title get() = env(Variable.Commit.title)

		/**
		 * The commit [message] without its first line, if it is shorter than 100 characters.
		 *
		 * Otherwise, same as [message].
		 */
		val description get() = env(Variable.Commit.description)

		/**
		 * The SHA of the current commit.
		 */
		val sha get() = env(Variable.Commit.sha)

		/**
		 * The first eight characters of the [sha].
		 */
		val shortSha get() = env(Variable.Commit.shortSha)

		/**
		 * The timestamp of the commit in the ISO 8601 format.
		 */
		val timestamp get() = env(Variable.Commit.timestamp)

		/**
		 * Variables that relate to the reference this pipeline is building (e.g. a tag or a branch).
		 */
		object Ref {
			/**
			 * The tag or branch name.
			 */
			val name get() = env(Variable.Commit.Ref.name)

			/**
			 * `true` if this reference is protected.
			 */
			val protected get() = env(Variable.Commit.Ref.protected).toBoolean()

			/**
			 * [name] modified to fit in host names or URLs.
			 */
			val slug get() = env(Variable.Commit.Ref.slug)
		}
	}

	/**
	 * The container dependency proxy allows to cache containers in GitLab to avoid Docker Hub's rate limits.
	 */
	object DependencyProxy {

		/**
		 * Top-level group image prefix for pulling images.
		 */
		val groupImagePrefix get() = env(Variable.DependencyProxy.groupImagePrefix)

		/**
		 * The direct group image prefix for pulling images.
		 */
		val directGroupImagePrefix get() = env(Variable.DependencyProxy.directGroupImagePrefix)

		val username get() = env(Variable.DependencyProxy.username)

		val password get() = env(Variable.DependencyProxy.password)

		val server get() = env(Variable.DependencyProxy.server)

	}

	/**
	 * Only available if the pipeline runs during a deployment freeze window.
	 *
	 * `true` when available.
	 */
	val deployFreeze get() = env(Variable.deployFreeze).toBoolean()

	/**
	 * Only available if the job is executed in a disposable environment (something that is created only for this job
	 * and will be destroyed after the execution, all executors except `shell` and `ssh`).
	 *
	 * `true` when available.
	 */
	val disposable get() = env(Variable.disposable).toBoolean()

	val shared get() = env(Variable.shared).toBoolean()

	object Environment {

		/**
		 * The name of the environment for this job.
		 */
		val name get() = env(Variable.Environment.name)

		/**
		 * Simplified [name] suitable for DNS, URLs, etc.
		 */
		val slug get() = env(Variable.Environment.slug)

		/**
		 * URL of the environment.
		 */
		val url get() = env(Variable.Environment.url)

		/**
		 * The action of this environment.
		 *
		 * Can be `start`, `prepare` or `stop`.
		 */
		val action get() = env(Variable.Environment.action)

		/**
		 * The deployment tier for this job.
		 */
		val tier get() = env(Variable.Environment.tier)
	}

	/**
	 * Whether FIPS mode is enabled in the GitLab instance.
	 */
	val fipsMode get() = env(Variable.fipsMode).toBoolean()

	/**
	 * Only available if the pipeline's project has an open requirement.
	 *
	 * `true` when available.
	 */
	val hasOpenRequirements get() = env(Variable.hasOpenRequirements).toBoolean()

	object Job {
		val id get() = env(Variable.Job.id)
		val image get() = env(Variable.Job.image)
		val jwt get() = env(Variable.Job.jwt)
		val manual get() = env(Variable.Job.manual)
		val name get() = env(Variable.Job.name)
		val stage get() = env(Variable.Job.stage)
		val status get() = env(Variable.Job.status)
		val token get() = env(Variable.Job.token)
		val url get() = env(Variable.Job.url)
		val startedAt get() = env(Variable.Job.startedAt)
	}

	val kubernetesActive get() = env(Variable.kubernetesActive).toBoolean()

	object Parallel {
		val index get() = env(Variable.Parallel.index)
		val total get() = env(Variable.Parallel.total)
	}

	val openMergeRequests get() = env(Variable.openMergeRequests)

	object Pages {
		val domain get() = env(Variable.Pages.domain)
		val url get() = env(Variable.Pages.url)
	}

	object Pipeline {
		val id get() = env(Variable.Pipeline.id)
		val iid get() = env(Variable.Pipeline.iid)
		val source get() = env(Variable.Pipeline.source)
		val triggered get() = env(Variable.Pipeline.triggered)
		val url get() = env(Variable.Pipeline.url)
		val createdAt get() = env(Variable.Pipeline.createdAt)
	}

	object Project {
		val directory get() = env(Variable.Project.directory)
		val id get() = env(Variable.Project.id)
		val name get() = env(Variable.Project.name)
		val namespace get() = env(Variable.Project.namespace)
		val path get() = env(Variable.Project.path)
		val pathSlug get() = env(Variable.Project.pathSlug)
		val languages get() = env(Variable.Project.languages)
		val rootNamespace get() = env(Variable.Project.rootNamespace)
		val title get() = env(Variable.Project.title)
		val url get() = env(Variable.Project.url)
		val visibility get() = env(Variable.Project.visibility)
		val classificationLabel get() = env(Variable.Project.classificationLabel)
		val cloneUrl get() = env(Variable.Project.cloneUrl)
	}

	object Registry {
		val image get() = env(Variable.Registry.image)
		val server get() = env(Variable.Registry.server)
		val username get() = env(Variable.Registry.username)
		val password get() = env(Variable.Registry.password)
	}

	object Runner {
		val description get() = env(Variable.Runner.description)
		val arch get() = env(Variable.Runner.arch)
		val id get() = env(Variable.Runner.id)
		val revision get() = env(Variable.Runner.revision)
		val shortToken get() = env(Variable.Runner.shortToken)
		val tags get() = env(Variable.Runner.tags)
		val version get() = env(Variable.Runner.version)
	}

	object Server {
		val version get() = env(Variable.Server.host)
		val name get() = env(Variable.Server.name)
		val port get() = env(Variable.Server.port)
		val protocol get() = env(Variable.Server.protocol)
		val revision get() = env(Variable.Server.revision)
		val url get() = env(Variable.Server.url)

		object Version {
			val major get() = env(Variable.Server.Version.major)
			val minor get() = env(Variable.Server.Version.minor)
			val patch get() = env(Variable.Server.Version.patch)
			val full get() = env(Variable.Server.Version.full)
		}
	}

	val features = env(Variable.features)

	object User {
		val id get() = env(Variable.User.id)
		val email get() = env(Variable.User.email)
		val login get() = env(Variable.User.login)
		val name get() = env(Variable.User.name)
	}

	val triggerPayload get() = env(Variable.triggerPayload)

	object MergeRequest {

		val approved get() = env(Variable.MergeRequest.approved)
		val assignees get() = env(Variable.MergeRequest.assignees)
		val id get() = env(Variable.MergeRequest.id)
		val iid get() = env(Variable.MergeRequest.iid)
		val labels get() = env(Variable.MergeRequest.labels)
		val milestone get() = env(Variable.MergeRequest.milestone)
		val refPath get() = env(Variable.MergeRequest.refPath)
		val title get() = env(Variable.MergeRequest.title)
		val eventType get() = env(Variable.MergeRequest.eventType)
		val diffId get() = env(Variable.MergeRequest.diffId)
		val diffBaseSha get() = env(Variable.MergeRequest.diffBaseSha)

		object Target {
			val branch get() = env(Variable.MergeRequest.Target.branch)
			val branchSha get() = env(Variable.MergeRequest.Target.branchSha)
			val projectId get() = env(Variable.MergeRequest.Target.projectId)
			val projectPath get() = env(Variable.MergeRequest.Target.projectPath)
			val projectUrl get() = env(Variable.MergeRequest.Target.projectUrl)
		}

		object Source {
			val branch get() = env(Variable.MergeRequest.Source.branch)
			val branchSha get() = env(Variable.MergeRequest.Source.branchSha)
			val projectId get() = env(Variable.MergeRequest.Source.projectId)
			val projectPath get() = env(Variable.MergeRequest.Source.projectPath)
			val projectUrl get() = env(Variable.MergeRequest.Source.projectUrl)
		}
	}

	object ExternalPullRequest {
		val iid get() = env(Variable.ExternalPullRequest.iid)

		object Target {
			val repository get() = env(Variable.ExternalPullRequest.Target.repository)
			val branch get() = env(Variable.ExternalPullRequest.Target.branch)
			val branchSha get() = env(Variable.ExternalPullRequest.Target.branchSha)
		}

		object Source {
			val repository get() = env(Variable.ExternalPullRequest.Source.repository)
			val branch get() = env(Variable.ExternalPullRequest.Source.branch)
			val branchSha get() = env(Variable.ExternalPullRequest.Source.branchSha)
		}
	}
}
