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
import opensavvy.gitlab.ci.yaml.yamlMap

/**
 * Configures files or directories to cache between jobs.
 *
 * Caches are shared between pipelines and jobs, but by default not shared between protected and unprotected
 * branches. They are restored before artifacts, and limited to a maximum of four different caches.
 *
 * ### Example
 *
 * ```kotlin
 * val build by job {
 *     script {
 *         shell("make build")
 *     }
 *
 *     cache {
 *         key("binaries-cache")
 *         include("binaries/")
 *         include(".config")
 *     }
 * }
 * ```
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cache)
 */
@GitLabCiDsl
class Cache : YamlExport {
	private val includes = ArrayList<String>()

	private var key: String? = null
	private val keyFiles = ArrayList<String>()
	private val keyFilesCommits = ArrayList<String>()
	private var keyPrefix: String? = null

	private var untracked: Boolean? = null
	private var unprotect: Boolean? = null
	private var rule: When? = null
	private var policy: CachePolicy? = null
	private val fallbackKeys = ArrayList<String>()

	/**
	 * Chooses which files or directories to cache.
	 *
	 * Paths are relative to the project directory and can use wildcards with glob patterns.
	 * You can call this method multiple times to include multiple paths.
	 *
	 * Includes files even if they are untracked or in your `.gitignore` file.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachepaths)
	 */
	@GitLabCiDsl
	fun include(path: String) {
		includes += path
	}

	/**
	 * Gives this cache a unique identifying key.
	 *
	 * All jobs that use the same cache key use the same cache, including in different pipelines.
	 * If not set, the default key is `default`.
	 *
	 * Mutually exclusive with [keyFile] and [keyFileCommit]: if any of them are used, this value is ignored.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachekey)
	 */
	@GitLabCiDsl
	fun key(key: String) {
		this.key = key
	}

	/**
	 * Generates a new cache key when the content of [path] changes.
	 *
	 * If the content remains unchanged, the cache key remains consistent across branches and pipelines.
	 * You can call this method at most twice.
	 *
	 * Mutually exclusive with [keyFileCommit].
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachekeyfiles)
	 */
	@GitLabCiDsl
	fun keyFile(path: String) {
		keyFiles += path
	}

	/**
	 * Generates a new cache key when the latest commit changes for [path].
	 *
	 * Unlike [keyFile], the key changes whenever [path] has a new commit, even if its content remains identical.
	 * You can call this method at most twice.
	 *
	 * Mutually exclusive with [keyFile].
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachekeyfiles_commits)
	 */
	@GitLabCiDsl
	fun keyFileCommit(path: String) {
		keyFilesCommits += path
	}

	/**
	 * Combines [prefix] with the SHA computed from [keyFile].
	 *
	 * If none of the files declared with [keyFile] exist or changed, the prefix is added to `default`.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachekeyprefix)
	 */
	@GitLabCiDsl
	fun keyPrefix(prefix: String) {
		this.keyPrefix = prefix
	}

	/**
	 * Caches all files that are untracked in the Git repository, in addition to any path declared with [include].
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cacheuntracked)
	 */
	@GitLabCiDsl
	fun untracked(untracked: Boolean) {
		this.untracked = untracked
	}

	/**
	 * Shares this cache between protected and unprotected branches.
	 *
	 * When set to `true`, users without access to protected branches can read and write to cache keys used by
	 * protected branches.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cacheunprotect)
	 */
	@GitLabCiDsl
	fun unprotect(unprotect: Boolean) {
		this.unprotect = unprotect
	}

	/**
	 * Defines when to save the cache, based on the status of the job.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachewhen)
	 */
	@GitLabCiDsl
	fun rule(rule: When) {
		this.rule = rule
	}

	/**
	 * Changes the upload and download behavior of this cache.
	 *
	 * By default, the job downloads the cache when it starts, and uploads changes to the cache when it ends.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachepolicy)
	 */
	@GitLabCiDsl
	fun policy(policy: CachePolicy) {
		this.policy = policy
	}

	/**
	 * Adds a key to try to restore the cache from, if no cache is found for [key].
	 *
	 * Caches are retrieved in the order they were declared with this method.
	 *
	 * ### External resources
	 *
	 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachefallback_keys)
	 */
	@GitLabCiDsl
	fun fallbackKey(key: String) {
		fallbackKeys += key
	}

	private fun keyYaml(): Yaml? = when {
		keyFiles.isNotEmpty() || keyFilesCommits.isNotEmpty() || keyPrefix != null -> yamlMap {
			addNotEmpty("files", keyFiles)
			addNotEmpty("files_commits", keyFilesCommits)
			addNotNull("prefix", keyPrefix)
		}

		key != null -> yaml(key!!)
		else -> null
	}

	override fun toYaml(): Yaml = yamlMap {
		add("paths", includes)
		keyYaml()?.let { add("key", it) }
		addNotNull("untracked", untracked)
		addNotNull("unprotect", unprotect)
		addNotNull("when", rule)
		addNotNull("policy", policy)
		addNotEmpty("fallback_keys", fallbackKeys)
	}
}

/**
 * The upload and download behavior of a [Cache].
 *
 * ### External resources
 *
 * - [Official documentation](https://docs.gitlab.com/ci/yaml/#cachepolicy)
 */
enum class CachePolicy(private val value: String) : YamlExport {
	/**
	 * Only downloads the cache when the job starts, never uploads changes when it finishes.
	 */
	Pull("pull"),

	/**
	 * Only uploads the cache when the job finishes, never downloads it when it starts.
	 */
	Push("push"),

	/**
	 * Downloads the cache when the job starts, and uploads changes to it when the job ends.
	 */
	PullPush("pull-push"),
	;

	override fun toYaml(): Yaml = yaml(value)
}
