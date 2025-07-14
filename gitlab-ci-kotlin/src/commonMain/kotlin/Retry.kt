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

import opensavvy.gitlab.ci.yaml.Yaml
import opensavvy.gitlab.ci.yaml.yaml
import opensavvy.gitlab.ci.yaml.yamlList
import opensavvy.gitlab.ci.yaml.yamlMap

/**
 * Configuration for job retry behavior.
 *
 * ### Example
 *
 * ```kotlin
 * val test by job {
 *     retry(3) {
 *         whenType(RetryWhen.Always)
 *         onExitCode(127)
 *     }
 * }
 * ```
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#retry)
 */
@GitLabCiDsl
class Retry internal constructor(
	/**
	 * The maximum number of times a job is retried if it fails.
	 * Supported values: 0, 1, or 2.
	 */
	val max: Int,
) : YamlExport {

	init {
		require(max in 0..2) { "Retry max must be 0, 1, or 2, but was $max" }
	}

	private var causes: MutableSet<RetryCause>? = null
	private var exitCodes: MutableSet<Int>? = null

	/**
	 * Specifies which failure types to retry on.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * retry(2) {
	 *     whenType(RetryWhen.RunnerSystemFailure)
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#retrywhen)
	 */
	@GitLabCiDsl
	fun on(type: RetryCause) {
		if (causes == null) {
			causes = mutableSetOf()
		}
		causes?.add(type)
	}

	/**
	 * Specifies which exit codes to retry on.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * retry(2) {
	 *     onExitCode(137)
	 * }
	 * ```
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#retryexit_codes)
	 */
	@GitLabCiDsl
	fun onExitCode(code: Int) {
		if (exitCodes == null) {
			exitCodes = mutableSetOf()
		}
		exitCodes?.add(code)
	}

	override fun toYaml(): Yaml {
		// If we only have max, return a simple scalar
		if (causes == null && exitCodes == null) {
			return yaml(max.toLong())
		}

		// Otherwise, return a map with max and optional when/exit_codes
		val elements = HashMap<Yaml, Yaml>()
		elements[yaml("max")] = yaml(max.toLong())

		causes?.let { types ->
			if (types.size == 1) {
				elements[yaml("when")] = types.first().toYaml()
			} else if (types.isNotEmpty()) {
				elements[yaml("when")] = yamlList(types.toList())
			}
		}

		exitCodes?.let { codes ->
			if (codes.size == 1) {
				elements[yaml("exit_codes")] = yaml(codes.first().toLong())
			} else if (codes.isNotEmpty()) {
				elements[yaml("exit_codes")] = yamlList(codes.map { yaml(it.toLong()) })
			}
		}

		return yamlMap(elements)
	}
}

/**
 * Failure types that can be used with [Retry.on].
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ee/ci/yaml/#retrywhen)
 */
enum class RetryCause(private val code: String) : YamlExport {
	/**
	 * Retry on any failure (default).
	 */
	Always("always"),

	/**
	 * Retry when the failure reason is unknown.
	 */
	UnknownFailure("unknown_failure"),

	/**
	 * Retry when the script failed or the runner failed to pull the Docker image.
	 */
	ScriptFailure("script_failure"),

	/**
	 * Retry on API failure.
	 */
	ApiFailure("api_failure"),

	/**
	 * Retry when the job got stuck or timed out.
	 */
	StuckOrTimeoutFailure("stuck_or_timeout_failure"),

	/**
	 * Retry if there is a runner system failure (for example, job setup failed).
	 */
	RunnerSystemFailure("runner_system_failure"),

	/**
	 * Retry if the runner is unsupported.
	 */
	RunnerUnsupported("runner_unsupported"),

	/**
	 * Retry if a delayed job could not be executed.
	 */
	StaleSchedule("stale_schedule"),

	/**
	 * Retry if the script exceeded the maximum execution time set for the job.
	 */
	JobExecutionTimeout("job_execution_timeout"),

	/**
	 * Retry if the job is archived and can't be run.
	 */
	ArchivedFailure("archived_failure"),

	/**
	 * Retry if the job failed to complete prerequisite tasks.
	 */
	UnmetPrerequisites("unmet_prerequisites"),

	/**
	 * Retry if the scheduler failed to assign the job to a runner.
	 */
	SchedulerFailure("scheduler_failure"),

	/**
	 * Retry if there is an unknown job problem.
	 */
	DataIntegrityFailure("data_integrity_failure"),
	;

	override fun toYaml() = yaml(code)
}
