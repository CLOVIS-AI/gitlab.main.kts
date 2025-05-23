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

package opensavvy.gitlab.ci.yaml

import opensavvy.gitlab.ci.YamlExport
import opensavvy.gitlab.ci.utils.plusAssign

sealed class Yaml : YamlExport {

	abstract fun toYamlString(indentation: Int): CharSequence
	fun toYamlString() = toYamlString(0).toString()

	override fun toYaml() = this

	sealed class Scalar : Yaml() {

		data class StringLiteral(val value: String) : Scalar() {
			override fun toYamlString(indentation: Int): CharSequence {
				val lines = value.split("\n")

				return if (lines.size == 1) {
					if (":" in value)
						"'$value'"
					else
						value.ifBlank { "''" }
				} else {
					val builder = StringBuilder()
					builder += "|\n"

					for (line in lines) {
						builder indent indentation + 1
						builder += line
						builder += "\n"
					}

					builder
				}
			}
		}

		data class IntegerLiteral(val value: Long) : Scalar() {
			override fun toYamlString(indentation: Int) = value.toString()
		}

		data class FloatingLiteral(val value: Double) : Scalar() {
			override fun toYamlString(indentation: Int) = value.toString()
		}

		data class BooleanLiteral(val value: Boolean) : Scalar() {
			override fun toYamlString(indentation: Int) = value.toString()
		}

		object NullLiteral : Scalar() {
			override fun toYamlString(indentation: Int) = "null"
		}

	}

	sealed class Collection : Yaml() {

		data class ListLiteral(val children: List<Yaml>) : Collection() {

			constructor(vararg child: Yaml) : this(listOf(*child))

			override fun toYamlString(indentation: Int): CharSequence {
				val builder = StringBuilder()

				for (child in children) {
					builder += "\n"
					builder indent indentation
					builder += "- "
					builder += child.toYamlString(indentation + 1)
				}

				if (children.isEmpty()) {
					builder += "[]"
				}

				return builder
			}
		}

		data class MapLiteral(val contents: Map<Yaml, Yaml>) : Collection() {

			constructor(vararg child: Pair<Yaml, Yaml>) : this(mapOf(*child))

			constructor(contents: List<Pair<Yaml, Yaml>>) : this(contents.toMap())

			override fun toYamlString(indentation: Int): CharSequence {
				val builder = StringBuilder()

				for ((key, value) in contents) {
					if (key !is Scalar)
						TODO("Only scalar keys are supported at the moment; you provided $key")

					builder += "\n"
					builder indent indentation
					builder += key.toYamlString(indentation + 1)
					builder += ": "
					builder += value.toYamlString(indentation + 1)
				}

				return builder
			}
		}
	}
}

private infix fun StringBuilder.indent(indentation: Int) {
	repeat(indentation) {
		this += "  "
	}
}
