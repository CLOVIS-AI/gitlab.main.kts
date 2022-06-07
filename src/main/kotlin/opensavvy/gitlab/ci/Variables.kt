package opensavvy.gitlab.ci

/**
 * Predefined variables in GitLab CI.
 *
 * Read more in the [GitLab documentation](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html).
 */
@Suppress("unused")
object Variables {

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
