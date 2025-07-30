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

import opensavvy.gitlab.ci.script.CommandDsl
import opensavvy.gitlab.ci.script.shell

/**
 * Integrates [Pacman](https://wiki.archlinux.org/title/Pacman) into your build.
 *
 * This plugin adds the [pacman] extension to scripts:
 *
 * ```kotlin
 * val deploy by job {
 *     image("archlinux:latest")
 *
 *     script {
 *         pacman.sync("git")
 *         shell("git describe")
 *     }
 * }
 * ```
 */
class Pacman private constructor(private val dsl: CommandDsl) {

	/**
	 * Executes `pacman -Syuu` [pkg].
	 */
	fun sync(vararg pkg: String) = with(dsl) {
		shell("pacman -Syuu ${pkg.joinToString(separator = " ")}")
	}

	companion object {
		/**
		 * Accesses Pacman commands.
		 *
		 * To use this plugin, the `pacman` command must be available to this job.
		 *
		 * @see Pacman
		 */
		val CommandDsl.pacman get() = Pacman(this)
	}
}
