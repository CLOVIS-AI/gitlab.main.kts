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

package opensavvy.gitlab.ci.script

import opensavvy.gitlab.ci.GitLabCiDsl
import opensavvy.gitlab.ci.YamlExport
import opensavvy.gitlab.ci.yaml.Yaml
import org.intellij.lang.annotations.Language

@GitLabCiDsl
abstract class Command : YamlExport

@GitLabCiDsl
data class Shell(@Language("Sh") val command: String) : Command() {
	override fun toYaml() = Yaml.Scalar.StringLiteral(command)
}

@GitLabCiDsl
fun CommandDsl.shell(@Language("Sh") command: String) {
	Shell(command)
		.also { +it }
}
